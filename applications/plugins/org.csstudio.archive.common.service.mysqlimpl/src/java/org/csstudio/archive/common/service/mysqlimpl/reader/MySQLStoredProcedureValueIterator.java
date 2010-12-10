package org.csstudio.archive.common.service.mysqlimpl.reader;
//package org.csstudio.archivereader.rdb;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//import org.apache.log4j.Logger;
//import org.csstudio.platform.data.INumericMetaData;
//import org.csstudio.platform.data.ISeverity;
//import org.csstudio.platform.data.ITimestamp;
//import org.csstudio.platform.data.IValue;
//import org.csstudio.platform.data.ValueFactory;
//import org.csstudio.platform.logging.CentralLogger;
//
///**
// *
// * TODO (bknerr) : Consider refactoring to Abstract
// *
// * @author bknerr
// * @author $Author: bknerr $
// * @version $Revision: 1.7 $
// * @since 22.10.2010
// */
//public class MySQLStoredProcedureValueIterator extends AbstractStoredProcedureValueIterator {
//
//
//    private static final Logger LOG = CentralLogger.getInstance()
//            .getLogger(MySQLStoredProcedureValueIterator.class);
//
//    MySQLStoredProcedureValueIterator(final RDBArchiveReader reader,
//                                      final String stored_procedure,
//                                      final int channel_id,
//                                      final ITimestamp start,
//                                      final ITimestamp end,
//                                      final int count) throws Exception {
//        super(reader, channel_id);
//        this.stored_procedure = stored_procedure;
//        executeProcedure(start, end, count);
//    }
//
//    private void executeProcedure(final ITimestamp start, final ITimestamp end, final int count) {
//        ResultSet rs = null;
//        PreparedStatement cStmt = null;
//        try {
//            final RDBUtil rdb = reader.getRDB();
//            final Connection connection = rdb.getConnection();
//            // callable statement does not work properly > SQL function unknown error, seems to be a bug
//            cStmt = connection.prepareStatement("{call " + stored_procedure + "(?, ?, ?, ?)}");
//
//            reader.addForCancellation(cStmt);
//
//            cStmt.setInt(1, channel_id);
//            cStmt.setTimestamp(2, TimeWarp.getSQLTimestamp(start));
//            cStmt.setTimestamp(3, TimeWarp.getSQLTimestamp(end));
//            cStmt.setInt(4, getExpOfNextHigherPowerOfTwo(count));
//
//            cStmt.setFetchDirection(ResultSet.FETCH_FORWARD);
//            cStmt.setFetchSize(1000);
//
//            LOG.info(cStmt.toString());
//
//            final boolean hadResults = cStmt.execute();
//            if (hadResults) {
//                rs = cStmt.getResultSet();
//                final ResultSetMetaData meta = rs.getMetaData();
//                final int N = meta.getColumnCount();
//                if (N == 9) {
//                    values = decodeOptimizedTable(rs);
//                } else {
//                    values = decodeSampleTable(rs);
//                }
//                // Initialize iterator for first value
//                if (values.length > 0) {
//                    index = 0;
//                    // else: No data, leave as -1
//                }
//            }
//        } catch (final SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (final Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            //            if (rs != null) {
//            //                try {
//            //                    rs.close();
//            //                } catch (final SQLException e) { // Ignore
//            //                }
//            //                rs = null;
//            //            }
//            if (cStmt != null) {
//                try {
//                    reader.removeFromCancellation(cStmt);
//                    cStmt.close();
//                } catch (final SQLException e) { // Ignore
//                }
//                cStmt = null;
//            }
//        }
//    }
//
//    /** Decode samples from 'optimized' table with min/max/average
//     *  @param result ResultSet
//     *  @return IValue array of samples
//     *  @throws Exception on error
//     */
//    private IValue[] decodeOptimizedTable(final ResultSet result) throws Exception {
//        final ArrayList<IValue> tmp_values = new ArrayList<IValue>();
//
//        // Need numeric meta data or nothing
//        final INumericMetaData meta = this.meta instanceof INumericMetaData ? (INumericMetaData) this.meta
//                : null;
//
//        // Row with min/max/average data:
//        // WB: 1, SMPL_TIME: 2010/01/22 21:07:18.772633666, SEVERITY_ID: null, STATUS_ID: null, MIN_VAL: 8.138729867823713E-8, MAX_VAL: 6.002717327646678E-7, AVG_VAL: 8.240168908036992E-8, STR_VAL: null, CNT: 3611
//        // Row with String value:
//        // WB: -1, SMPL_TIME: 2010/01/28 11:14:11.086000000, SEVERITY_ID: 2, STATUS_ID: 2, MIN_VAL: null, MAX_VAL: null, AVG_VAL: null, STR_VAL: Archive_Off, CNT: 1
//        // i.e. Columns 1 WB, 2 SMPL_TIME, 3 SEVERITY_ID, 4 STATUS_ID, 5 MIN_VAL, 6 MAX_VAL, 7 AVG_VAL, 8 STR_VAL, 9 CNT
//        while (result.next()) {
//            // Time stamp
//            final ITimestamp time = TimeWarp.getCSSTimestamp(result.getTimestamp(2));
//
//            // Get severity/status
//            ISeverity severity = reader.getSeverity(result.getInt(3));
//            final String status;
//            if (result.wasNull()) {
//                severity = ValueFactory.createOKSeverity();
//                status = severity.toString();
//            } else {
//                status = reader.getStatus(result.getInt(4));
//                severity = filterSeverity(severity, status);
//            }
//
//            // WB==-1 indicates a String sample
//            final IValue value;
//            if (result.getInt(1) < 0) {
//                value = ValueFactory.createStringValue(time,
//                                                       severity,
//                                                       status,
//                                                       IValue.Quality.Original,
//                                                       new String[] { result.getString(8) });
//            } else { // Only one value within averaging bucket?
//                final int cnt = result.getInt(9);
//                if (cnt == 1) {
//                    value = ValueFactory.createDoubleValue(time,
//                                                           severity,
//                                                           status,
//                                                           meta,
//                                                           IValue.Quality.Original,
//                                                           new double[] { result.getDouble(7) });
//                } else {
//                    value = ValueFactory
//                            .createMinMaxDoubleValue(time,
//                                                     severity,
//                                                     status,
//                                                     meta,
//                                                     IValue.Quality.Interpolated,
//                                                     new double[] { result.getDouble(7) },
//                                                     result.getDouble(5),
//                                                     result.getDouble(6));
//                }
//            }
//            tmp_values.add(value);
//        }
//        // Convert to plain array
//        final IValue values[] = tmp_values.toArray(new IValue[tmp_values.size()]);
//        return values;
//    }
//
//    /** Decode samples from SAMPLE table
//     *  @param result ResultSet
//     *  @return IValue array of samples
//     *  @throws Exception on error, including cancellation
//     */
//    private IValue[] decodeSampleTable(final ResultSet result) throws Exception {
//        final ArrayList<IValue> tmp_values = new ArrayList<IValue>();
//        while (result.next()) {
//            final IValue value = decodeSampleTableValue(result);
//            tmp_values.add(value);
//        }
//        return tmp_values.toArray(new IValue[tmp_values.size()]);
//    }
//
//    /**
//     * TODO (bknerr) : get this method in a location sort of 'css math' or whatever
//     *
//     * Returns the exponent of the number being the first power of two equal or greater than\
//     * the given number.
//     *
//     * @param num
//     * @return the power of twos exponent that is the first equal or greater than num
//     */
//    private int getExpOfNextHigherPowerOfTwo(final int num) {
//        if (num <= 0) {
//            return 0;
//        }
//        int c = num;
//        int pow = 0;
//        while (c > 1) {
//            c >>= 1;
//            pow++;
//        }
//        return pow;
//    }
//
//    /** {@inheritDoc} */
//    public boolean hasNext()
//    {
//        return index >= 0;
//    }
//
//    /** {@inheritDoc} */
//    public IValue next() throws Exception
//    {
//        final IValue result = values[index];
//        ++index;
//        if (index >= values.length) {
//            index = -1;
//        }
//        return result;
//    }
//
//}
