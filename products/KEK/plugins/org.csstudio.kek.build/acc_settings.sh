VALID_ACCS="JPARC PFAR PF LINAC SUPERKEKB CERL JPARC_OFFICE PF_OFFICE LINAC_OFFICE SUPERKEKB_OFFICE"

JPARC_ADDR_LIST="jparc.kek.jp"
JPARC_ARCHIVE_URLS="jdbc:postgresql://jparc.kek.jp:5432/archive"
JPARC_ARCHIVE_ALIAS_1="J-PARC"
JPARC_ARCHIVE_NAMES_1="rdb"
JPARC_COLOR_DEF="/BOY Examples/color.def"
JPARC_FONT_DEF="/BOY Examples/font.def"

PFAR_ADDR_LIST="172.19.63.255 172.19.68.144 172.19.68.161 130.87.169.30"
PFAR_ARCHIVE_URLS="jdbc:postgresql://abcob12.kekb.kek.jp:5432/archive"
PFAR_ARCHIVE_ALIAS_1="PF-AR"
PFAR_ARCHIVE_NAMES_1="rdb"
PFAR_COLOR_DEF="/Operation/AR/color_AR.def"
PFAR_FONT_DEF="/Operation/AR/font_AR.def"
PFAR_SHARE_LINK_SRC_WIN_1='\\erlnas2w.kek.jp\opi\PF-AR'
PFAR_SHARE_LINK_SRC_1="/cont/epics314/app/PFAR/opi"
PFAR_SHARE_LINK_DEST_1="/Operation/AR"

PF_ADDR_LIST="172.28.255.255"
PF_ARCHIVE_URLS="jdbc:postgresql://pfrproc4.pfring.kek.jp:5432/archive"
PF_ARCHIVE_ALIAS_1="PF-Ring"
PF_ARCHIVE_NAMES_1="rdb"
PF_COLOR_DEF="/Operation/PF/color_PF.def"
PF_FONT_DEF="/Operation/PF/font_PF.def"
PF_SHARE_LINK_SRC_WIN_1='\\erlnas2w.kek.jp\opi\PF-Ring'
PF_SHARE_LINK_SRC_1="/pf/epics/app/opi/PF-Ring"
PF_SHARE_LINK_DEST_1="/Operation/PF"

LINAC_ADDR_LIST="172.19.95.255"
LINAC_ARCHIVE_URLS="xnds://lcba03.linac.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.linac.kek.jp:5432/archive"
LINAC_ARCHIVE_ALIAS_1="Linac (Channel Archiver)"
LINAC_ARCHIVE_ALIAS_2="Linac (RDB Archive Engine)"
LINAC_ARCHIVE_NAMES_1="Archive"
LINAC_ARCHIVE_NAMES_2="rdb"
LINAC_COLOR_DEF="/BOY Examples/color.def"
LINAC_FONT_DEF="/BOY Examples/font.def"

SUPERKEKB_ADDR_LIST="172.19.63.255"
SUPERKEKB_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_ARCHIVE_ALIAS_1="SuperKEKB"
SUPERKEKB_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.
SUPERKEKB_COLOR_DEF="/BOY Examples/color.def"
SUPERKEKB_FONT_DEF="/BOY Examples/font.def"

CERL_ADDR_LIST="172.28.79.255"
CERL_ARCHIVE_URLS="jdbc:postgresql://pfrproc5.kek.jp:9090/archive"
CERL_ARCHIVE_ALIAS_1="cERL"
CERL_ARCHIVE_NAMES_1="rdb"
CERL_COLOR_DEF="/Operation/cERL/color_cERL.def"
CERL_FONT_DEF="/Operation/cERL/font_cERL.def"
CERL_SHARE_LINK_SRC_WIN_1='\\erlnas2w.kek.jp\opi\cERL'
CERL_SHARE_LINK_SRC_1="/cerl/epics/app/opi/cERL"
CERL_SHARE_LINK_DEST_1="/Operation/cERL"

JPARC_OFFICE_ADDR_LIST="jparc-office.kek.jp"
JPARC_OFFICE_ARCHIVE_URLS="jdbc:postgresql://jparc-office.kek.jp:5432/archive"
JPARC_OFFICE_ARCHIVE_ALIAS_1="J-PARC (Office)"
JPARC_OFFICE_ARCHIVE_NAMES_1="rdb"
JPARC_OFFICE_COLOR_DEF="/BOY Examples/color.def"
JPARC_OFFICE_FONT_DEF="/BOY Examples/font.def"

PF_OFFICE_ADDR_LIST="127.0.0.1 pfconrg06.kek.jp pfconrg07.kek.jp erlserv1.kek.jp"
PF_OFFICE_ARCHIVE_URLS="jdbc:postgresql://pfrproc5.kek.jp:9080/archive jdbc:postgresql://abcob12.kek.jp:5432/archive jdbc:postgresql://pfrproc5.kek.jp:9090/archive"
PF_OFFICE_ARCHIVE_ALIAS_1="PF-Ring"
PF_OFFICE_ARCHIVE_ALIAS_2="PF-AR"
PF_OFFICE_ARCHIVE_ALIAS_3="cERL"
PF_OFFICE_ARCHIVE_NAMES_1="rdb"
PF_OFFICE_ARCHIVE_NAMES_2="rdb"
PF_OFFICE_ARCHIVE_NAMES_3="rdb"
PF_OFFICE_COLOR_DEF="/Operation/PF/color_PF.def"
PF_OFFICE_FONT_DEF="/Operation/PF/font_PF.def"
PF_OFFICE_SHARE_LINK_SRC_WIN_1='\\erlnas2w.kek.jp\opi\PF-AR'
PF_OFFICE_SHARE_LINK_SRC_1="/cont/epics314/app/PFAR/opi"
PF_OFFICE_SHARE_LINK_DEST_1="/Operation/AR"
PF_OFFICE_SHARE_LINK_SRC_WIN_2='\\erlnas2w.kek.jp\opi\PF-Ring'
PF_OFFICE_SHARE_LINK_SRC_2="/pf/epics/app/opi/PF-Ring"
PF_OFFICE_SHARE_LINK_DEST_2="/Operation/PF"
PF_OFFICE_SHARE_LINK_SRC_WIN_3='\\erlnas2w.kek.jp\opi\cERL'
PF_OFFICE_SHARE_LINK_SRC_3="/cerl/epics/app/opi/cERL"
PF_OFFICE_SHARE_LINK_DEST_3="/Operation/cERL"

LINAC_OFFICE_ADDR_LIST="172.19.95.255"
LINAC_OFFICE_ARCHIVE_URLS="xnds://www-linac2.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.kek.jp:5432/archive"
LINAC_OFFICE_ARCHIVE_ALIAS_1="Linac (Channel Archiver)"
LINAC_OFFICE_ARCHIVE_ALIAS_2="Linac (RDB Archive Engine)"
LINAC_OFFICE_ARCHIVE_NAMES_1="Archive" 
LINAC_OFFICE_ARCHIVE_NAMES_2="rdb"
LINAC_OFFICE_COLOR_DEF="/BOY Examples/color.def"
LINAC_OFFICE_FONT_DEF="/BOY Examples/font.def"

SUPERKEKB_OFFICE_ADDR_LIST="172.19.63.255"
SUPERKEKB_OFFICE_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_OFFICE_ARCHIVE_ALIAS_1="SuperKEKB"
SUPERKEKB_OFFICE_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_OFFICE_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.
SUPERKEKB_OFFICE_COLOR_DEF="/BOY Examples/color.def"
SUPERKEKB_OFFICE_FONT_DEF="/BOY Examples/font.def"

