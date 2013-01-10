package org.csstudio.ams.distributor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.configdb.FilterTObject;
import org.csstudio.ams.dbAccess.configdb.HistoryDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
import org.csstudio.ams.dbAccess.configdb.TopicTObject;
import org.csstudio.ams.dbAccess.configdb.UserGroupTObject;
import org.csstudio.ams.dbAccess.configdb.UserTObject;

class HistoryWriter {
	private static final String HISTORY_DEST_TYPE_SMS = "SMS";
	private static final String HISTORY_DEST_TYPE_VMAIL = "VMail";
	private static final String HISTORY_DEST_TYPE_EMAIL = "EMail";
	private static final String HISTORY_DEST_TYPE_JMS = "JMS";
	private static final String HISTORY_ACTION_TYPE_GROUP_REPLY = "group reply";
	private static final String HISTORY_ACTION_TYPE_JMS_REPLY = "jms reply";

	private static final Executor executor = Executors
			.newSingleThreadExecutor();

	static void logHistoryRplStart(final Connection conDb, final boolean bStart) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					HistoryTObject history = new HistoryTObject();

					history.setTimeNew(new Date(System.currentTimeMillis()));
					history.setType("Config Synch");

					if (bStart)
						history.setDescription("Distributor stops normal work, starts with config replication.");
					else
						history.setDescription("Distributor ends config replication, goes to normal work.");

					HistoryDAO.insert(conDb, history);
					Log.log(Log.INFO,
					// history.getHistoryID() + ". " + //auskommentiert
							history.getDescription());
				} catch (Exception ex) {
					Log.log(Log.FATAL, "exception at history logging start="
							+ bStart, ex);
				}
			}
		});
	}

	static void logHistorySend(final java.sql.Connection conDb,
			final MapMessage mapMsg, final String msgText, final int faTypeRef,
			final UserTObject user, final UserGroupTObject group,
			final int iReceiverPos, final int iMessageChainID,
			final int iPrefAlarmingTypeRR,// interesting if > 0 only
			final TopicTObject topicObj) throws Exception // INCLUDING -
															// JMSException,
	// SQLException
	{
		executor.execute(new Runnable() {
			@Override
			public void run() {
				String msgHost;
				try {
					msgHost = mapMsg.getString(AmsConstants.MSGPROP_HOST);
					String msgProc = mapMsg
							.getString(AmsConstants.MSGPROP_PROCESSID);
					String msgName = mapMsg
							.getString(AmsConstants.MSGPROP_NAME);
					String msgEventTime = mapMsg
							.getString(AmsConstants.MSGPROP_EVENTTIME);

					HistoryTObject history = new HistoryTObject();

					String description = "Action"
							+ (iMessageChainID > 0 ? "(ChainId "
									+ iMessageChainID + ")" : "") + " as \""
							+ msgText + "\" to ";

					if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL)
						history.setActionType("user"); // an user/an group
					else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_G)
						history.setActionType("group");
					else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_TO_JMS) {
						history.setActionType("jms");
					} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_GR
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_GR
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_GR)
						history.setActionType(HistoryWriter.HISTORY_ACTION_TYPE_GROUP_REPLY);
					else {
						description = "Action to ";
						history.setActionType("topic fat=" + faTypeRef);
					}

					history.setTimeNew(new Date(System.currentTimeMillis()));
					history.setType("Action");
					history.setMsgHost(msgHost);
					history.setMsgProc(msgProc);
					history.setMsgName(msgName);
					history.setMsgEventtime(msgEventTime);
					history.setDescription(description);

					if (group != null) {
						history.setGroupRef(group.getUserGroupID());
						history.setGroupName(group.getName());
						history.setReceiverPos(iReceiverPos);
					}

					if (user != null && topicObj == null) {
						history.setUserRef(user.getUserID());
						history.setUserName(user.getName());

						if (iPrefAlarmingTypeRR > 0) {
							if (iPrefAlarmingTypeRR == AmsConstants.USERFILTERALARMTYPE_SMS) {
								history.setDestType("SMS-pref");
								history.setDestAdress(user.getMobilePhone());
							} else if (iPrefAlarmingTypeRR == AmsConstants.USERFILTERALARMTYPE_VM) {
								history.setDestType("VMail-pref");
								history.setDestAdress(user.getPhone());
							} else if (iPrefAlarmingTypeRR == AmsConstants.USERFILTERALARMTYPE_MAIL) {
								history.setDestType("EMail-pref");
								history.setDestAdress(user.getEmail());
							}
						} else {
							if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_G
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_GR) {
								history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_SMS);
								history.setDestAdress(user.getMobilePhone());
							} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_VM
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_G
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_GR) {
								history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_VMAIL);
								history.setDestAdress(user.getPhone());
							} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_G
									|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_GR) {
								history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_EMAIL);
								history.setDestAdress(user.getEmail());
							}
						}
					}

					if (topicObj != null) {
						Log.log(Log.INFO,
								"DistributorWork.logHistorySend() topicObj!=null");
						history.setUserRef(topicObj.getID());
						history.setUserName(topicObj.getHumanReadableName()); /*- TODO Kein Feld f�r Topic vorhanden, kl�ren ob DB erweitert werden soll!? */
						history.setDestType("jms topic");
						history.setDestAdress(topicObj.getTopicName());
					}

					HistoryDAO.insert(conDb, history);
					Log.log(Log.INFO, /* history.getHistoryID() + ". " + */
							description + history.getActionType() + " user="
									+ history.getUserName() + " via "
									+ history.getDestType() + " dest= "
									+ history.getDestAdress());
				} catch (JMSException e) {
					Log.log(Log.FATAL, e.getMessage());
				} catch (SQLException e) {
					Log.log(Log.FATAL, e.getMessage());
				}
			}
		});
	}

	static void logHistoryGroupBlocked(final java.sql.Connection conDb,
			final MapMessage mapMsg, final String msgText, final int faTypeRef,
			final UserGroupTObject group, final int iMessageChainID,
			int iPrefAlarmingTypeRR,// interesting if > 0 only
			final TopicTObject topicObj) throws Exception // INCLUDING -
															// JMSException,
	// SQLException
	{
		executor.execute(new Runnable() {
			@Override
			public void run() {

				try {
					String msgHost = mapMsg
							.getString(AmsConstants.MSGPROP_HOST);
					String msgProc = mapMsg
							.getString(AmsConstants.MSGPROP_PROCESSID);
					String msgName = mapMsg
							.getString(AmsConstants.MSGPROP_NAME);
					String msgEventTime = mapMsg
							.getString(AmsConstants.MSGPROP_EVENTTIME);

					HistoryTObject history = new HistoryTObject();

					String description = "Blocked"
							+ (iMessageChainID > 0 ? "(ChainId "
									+ iMessageChainID + ")" : "") + " as \""
							+ msgText + "\" to ";

					if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_G) {
						history.setActionType("group");
					} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_GR
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_GR
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_GR) {
						history.setActionType("group reply");
						description = description + group.getName();
					} else {
						description = "Blocked ";
						history.setActionType("<unknown type: " + faTypeRef
								+ ">");
					}

					history.setTimeNew(new Date(System.currentTimeMillis()));
					history.setType("Blocked");
					history.setMsgHost(msgHost);
					history.setMsgProc(msgProc);
					history.setMsgName(msgName);
					history.setMsgEventtime(msgEventTime);
					history.setDescription(description);

					if (group != null) {
						history.setGroupRef(group.getUserGroupID());
						history.setGroupName(group.getName());
					}

					if (topicObj != null) {
						Log.log(Log.INFO,
								"DistributorWork.logHistorySend() topicObj!=null");
						history.setUserRef(topicObj.getID());
						history.setUserName(topicObj.getHumanReadableName()); /*- TODO Kein Feld für Topic vorhanden, klären ob DB erweitert werden soll!? */
						history.setDestType("jms topic");
						history.setDestAdress(topicObj.getTopicName());
					}

					HistoryDAO.insert(conDb, history);
					Log.log(Log.INFO, /* history.getHistoryID() + ". " + */
							description + history.getActionType() + " user="
									+ history.getUserName() + " via "
									+ history.getDestType() + " dest= "
									+ history.getDestAdress());

				} catch (SQLException e) {
					Log.log(Log.FATAL, e.getMessage());
				} catch (JMSException e) {
					Log.log(Log.FATAL, e.getMessage());
				}
			}
		});
	}

	static void logHistoryReply(final java.sql.Connection conDb, final String strType,
			final UserTObject user, final UserGroupTObject userGroup, final TopicTObject topic,
			final String txt, final int faTypeRef, int msgChainId, final int msgChainPos,
			final String replyType, final String replyAdress) throws Exception // INCLUDING
	// -
	// SQLException
	{
		executor.execute(new Runnable() {
			@Override
			public void run() {
				HistoryTObject history = new HistoryTObject();

				history.setDestType(replyType);
				history.setDestAdress(replyAdress);
				history.setReceiverPos(msgChainPos);

				history.setTimeNew(new Date(System.currentTimeMillis()));
				history.setType(strType);

				/*
				 * if (faTypeRef == FILTERACTIONTYPE_SMS || faTypeRef ==
				 * FILTERACTIONTYPE_VM || faTypeRef == FILTERACTIONTYPE_MAIL)
				 * history.setActionType("user"); // an user/an group else if (faTypeRef
				 * == FILTERACTIONTYPE_SMS_G || faTypeRef == FILTERACTIONTYPE_VM_G ||
				 * faTypeRef == FILTERACTIONTYPE_MAIL_G) history.setActionType("group");
				 * else if (faTypeRef == FILTERACTIONTYPE_SMS_GR || faTypeRef ==
				 * FILTERACTIONTYPE_VM_GR || faTypeRef == FILTERACTIONTYPE_MAIL_GR)
				 * history.setActionType("group reply");
				 */

				history.setDescription(txt);

				if (user != null) {
					history.setActionType(HistoryWriter.HISTORY_ACTION_TYPE_GROUP_REPLY);
					history.setUserRef(user.getUserID());
					history.setUserName(user.getName());

					if (faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_SMS_GR) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_SMS);
						history.setDestAdress(user.getMobilePhone());
					} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_VM
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_VM_GR) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_VMAIL);
						history.setDestAdress(user.getPhone());
					} else if (faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_G
							|| faTypeRef == AmsConstants.FILTERACTIONTYPE_MAIL_GR) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_EMAIL);
						history.setDestAdress(user.getEmail());
					}

					if (replyAdress != null) {
						history.setDestAdress(history.getDestAdress() + " (from "
								+ replyAdress + ")");
					}

				} else if (topic != null) {
					history.setActionType(HistoryWriter.HISTORY_ACTION_TYPE_JMS_REPLY);
					history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_JMS);
					history.setDestAdress(topic.getTopicName());
					if (replyAdress != null) {
						history.setDestAdress(history.getDestAdress() + " (from "
								+ replyAdress + ")");
					}
				} else {
					history.setDestType(replyType);
					if (replyAdress != null)
						history.setDestAdress("from " + replyAdress);
				}

				if (userGroup != null) {
					history.setGroupRef(userGroup.getUserGroupID());
					history.setGroupName(userGroup.getName());
				}

				try {
					HistoryDAO.insert(conDb, history);
				} catch (SQLException e) {
					Log.log(Log.FATAL, e.getMessage());
				}
			}
		});
		
		
	}

	static void logHistoryChangeStatus(final java.sql.Connection conDb,
			final UserTObject user, final String because, final UserGroupTObject userGroup,
			final String txt, final int status, final String reason, final String replyType,
			final String replyAdress) throws Exception // INCLUDING - SQLException
	{
		executor.execute(new Runnable() {
			@Override
			public void run() {

				HistoryTObject history = new HistoryTObject();

				history.setTimeNew(new Date(System.currentTimeMillis()));
				history.setType("Sign on/off");

				String strDesc1 = "Status " + status;

				if (status == 0)
					strDesc1 = "Sign off";
				else if (status == 1)
					strDesc1 = "Sign on";

				if (user != null) {
					history.setUserRef(user.getUserID());
					history.setUserName(user.getName());

					if (replyType.equals(AmsConstants.MSG_REPLY_TYPE_SMS)) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_SMS);
						history.setDestAdress(user.getMobilePhone());
					} else if (replyType.equals(AmsConstants.MSG_REPLY_TYPE_EMAIL)) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_EMAIL);
						history.setDestAdress(user.getEmail());
					} else if (replyType.equals(AmsConstants.MSG_REPLY_TYPE_VOICEMAIL)) {
						history.setDestType(HistoryWriter.HISTORY_DEST_TYPE_VMAIL);
						history.setDestAdress(user.getPhone());
					}

					if (replyAdress != null)
						history.setDestAdress(history.getDestAdress() + " (from "
								+ replyAdress + ")");
				} else {
					history.setDestType(replyType);
					if (replyAdress != null)
						history.setDestAdress("from " + replyAdress);
				}

				String strDesc2 = null;
				if (because == null) {
					strDesc2 = " successful.";
					// history.setActionType("");
				} else
					strDesc2 = " failed because: " + because;

				if (strDesc1 != null) {
					history.setDescription(strDesc1 + " by " + replyType + strDesc2
							+ " \"" + txt + " " + (reason == null ? "" : reason) + "\"");
				}

				if (userGroup != null) {
					history.setGroupRef(userGroup.getUserGroupID());
					history.setGroupName(userGroup.getName());
				}

				try {
					HistoryDAO.insert(conDb, history);
				} catch (SQLException e) {
					Log.log(Log.FATAL, e.getMessage());
				}
			
			}
		});
	}

	static void logMessage(final java.sql.Connection localAppDb, final MapMessage msg,
			final FilterTObject filter, final int iFilterId) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String msgHost = msg.getString(AmsConstants.MSGPROP_HOST);
					final String msgProc = msg
							.getString(AmsConstants.MSGPROP_PROCESSID);
					final String msgName = msg.getString(AmsConstants.MSGPROP_NAME);
					final String msgEventTime = msg
							.getString(AmsConstants.MSGPROP_EVENTTIME);
					final HistoryTObject history = new HistoryTObject();
					history.setTimeNew(new Date(System.currentTimeMillis()));
					history.setType("Message");
					history.setMsgHost(msgHost);
					history.setMsgProc(msgProc);
					history.setMsgName(msgName);
					history.setMsgEventtime(msgEventTime);
					
					final String description = "Message filtered by "
							+ iFilterId + " - "
							+ (filter == null ? "filter not there" : filter.getName())
							+ "." + " Msg: " + Utils.getMessageString(msg);
					history.setDescription(description);
					
					HistoryDAO.insert(localAppDb, history);
				} catch (JMSException e) {
					Log.log(Log.FATAL, e.getMessage());
				} catch (SQLException e) {
					Log.log(Log.FATAL, e.getMessage());
				}
			}
		});

	}

}
