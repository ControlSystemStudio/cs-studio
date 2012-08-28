VALID_ACCS="JPARC PFAR PF LINAC SUPERKEKB CERL JPARC_OFFICE PFAR_OFFICE PF_OFFICE LINAC_OFFICE SUPERKEKB_OFFICE CERL_OFFICE"

JPARC_ADDR_LIST="jparc.kek.jp"
JPARC_ARCHIVE_URLS="jdbc:postgresql://jparc.kek.jp:5432/archive"
JPARC_ARCHIVE_NAMES_1="rdb"
JPARC_COLOR_DEF="/BOY Examples/color.def"
JPARC_FONT_DEF="/BOY Examples/font.def"

PFAR_ADDR_LIST="172.19.63.255 172.19.68.144 172.19.68.161 130.87.169.30"
PFAR_ARCHIVE_URLS="xnds://pfconrg06.kek.jp:8080/archive/cgi/ArchiveDataServer.cgi xnds://pfconrg07.kek.jp:8082/archive/cgi/ArchiveDataServer.cgi"
PFAR_ARCHIVE_NAMES_1="Archive"
PFAR_ARCHIVE_NAMES_2="Archive"
PFAR_COLOR_DEF="/Operation/AR/color_AR.def"
PFAR_FONT_DEF="/Operation/AR/font_AR.def"
PFAR_SHARE_LINK_SRC_WIN=
PFAR_SHARE_LINK_SRC="/cont/epics314/app/PFAR/OP/CSS/Operation"
PFAR_SHARE_LINK_DEST="/Operation/AR"

PF_ADDR_LIST="172.28.255.255"
PF_ARCHIVE_URLS="xnds://pfrproc2.pfring.kek.jp/archive/cgi/ArchiveDataServer.cgi"
PF_ARCHIVE_NAMES_1="Archive"
PF_COLOR_DEF="/Operation/PF/color_PF.def"
PF_FONT_DEF="/Operation/PF/font_PF.def"
PF_SHARE_LINK_SRC_WIN='\\pfrdisk1.pfring.kek.jp\epics\app\OP\CSS\OP-Workspace\PF'
PF_SHARE_LINK_SRC="/pf/epics/app/OP/CSS/OP-Workspace/PF"
PF_SHARE_LINK_DEST="/Operation/PF"

LINAC_ADDR_LIST="172.19.95.255"
LINAC_ARCHIVE_URLS="xnds://lcba03.linac.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.linac.kek.jp:5432/archive"
LINAC_ARCHIVE_NAMES_1="Archive"
LINAC_ARCHIVE_NAMES_2="rdb"
LINAC_COLOR_DEF="/BOY Examples/color.def"
LINAC_FONT_DEF="/BOY Examples/font.def"

SUPERKEKB_ADDR_LIST="172.19.63.255"
SUPERKEKB_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.
SUPERKEKB_COLOR_DEF="/BOY Examples/color.def"
SUPERKEKB_FONT_DEF="/BOY Examples/font.def"

CERL_ADDR_LIST="172.28.79.255"
CERL_ARCHIVE_URLS="xnds://pfconrg06.kek.jp:8080/archive/cgi/ArchiveDataServer.cgi xnds://pfconrg07.kek.jp:8082/archive/cgi/ArchiveDataServer.cgi"
CERL_ARCHIVE_NAMES_1="Archive"
CERL_ARCHIVE_NAMES_2="Archive"
CERL_COLOR_DEF="/Operation/cERL/color_cERL.def"
CERL_FONT_DEF="/Operation/cERL/font_cERL.def"
CERL_SHARE_LINK_SRC_WIN='\\erlnas2.cerl.kek.jp\public\EPICS\CSS\Operation\cERL'
CERL_SHARE_LINK_SRC="/cerl/epics/app/OP/CSS/OP-Workspace/cERL"
CERL_SHARE_LINK_DEST="/Operation/cERL"

JPARC_OFFICE_ADDR_LIST="jparc-office.kek.jp"
JPARC_OFFICE_ARCHIVE_URLS="jdbc:postgresql://jparc-office.kek.jp:5432/archive"
JPARC_OFFICE_ARCHIVE_NAMES_1="rdb"
JPARC_OFFICE_COLOR_DEF="/BOY Examples/color.def"
JPARC_OFFICE_FONT_DEF="/BOY Examples/font.def"

PFAR_OFFICE_ADDR_LIST="130.87.169.57 130.87.169.30 130.87.169.228"
PFAR_OFFICE_ARCHIVE_URLS="xnds://pfconrg06.kek.jp:8080/archive/cgi/ArchiveDataServer.cgi xnds://pfconrg07.kek.jp:8082/archive/cgi/ArchiveDataServer.cgi"
PFAR_OFFICE_ARCHIVE_NAMES_1="Archive"
PFAR_OFFICE_ARCHIVE_NAMES_2="Archive"
PFAR_OFFICE_COLOR_DEF="/Operation/AR/color_AR.def"
PFAR_OFFICE_FONT_DEF="/Operation/AR/font_AR.def"
PFAR_OFFICE_SHARE_LINK_SRC_WIN='\\pfrnas3.kek.jp\public\public\EPICS\CSS\Operation\AR'
PFAR_OFFICE_SHARE_LINK_SRC="/pf/epics/app/OP/CSS/OP-Workspace/AR"
PFAR_OFFICE_SHARE_LINK_DEST="/Operation/AR"

PF_OFFICE_ADDR_LIST="130.87.169.57 130.87.169.30 130.87.169.228"
PF_OFFICE_ARCHIVE_URLS="xnds://pfconrg06.kek.jp:8080/archive/cgi/ArchiveDataServer.cgi xnds://pfconrg07.kek.jp:8082/archive/cgi/ArchiveDataServer.cgi"
PF_OFFICE_ARCHIVE_NAMES_1="Archive"
PF_OFFICE_ARCHIVE_NAMES_2="Archive"
PF_OFFICE_COLOR_DEF="/Operation/PF/color_PF.def"
PF_OFFICE_FONT_DEF="/Operation/PF/font_PF.def"
PF_OFFICE_SHARE_LINK_SRC_WIN='\\pfrnas3.kek.jp\public\public\EPICS\CSS\Operation\PF'
PF_OFFICE_SHARE_LINK_SRC="/pf/epics/app/OP/CSS/OP-Workspace/PF"
PF_OFFICE_SHARE_LINK_DEST="/Operation/PF"

LINAC_OFFICE_ADDR_LIST="172.19.95.255"
LINAC_OFFICE_ARCHIVE_URLS="xnds://www-linac2.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.kek.jp:5432/archive"
LINAC_OFFICE_ARCHIVE_NAMES_1="Archive" 
LINAC_OFFICE_ARCHIVE_NAMES_2="rdb"
LINAC_OFFICE_COLOR_DEF="/BOY Examples/color.def"
LINAC_OFFICE_FONT_DEF="/BOY Examples/font.def"

SUPERKEKB_OFFICE_ADDR_LIST="172.19.63.255"
SUPERKEKB_OFFICE_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_OFFICE_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_OFFICE_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.
SUPERKEKB_OFFICE_COLOR_DEF="/BOY Examples/color.def"
SUPERKEKB_OFFICE_FONT_DEF="/BOY Examples/font.def"

CERL_OFFICE_ADDR_LIST="130.87.169.57 130.87.169.30 130.87.169.228"
CERL_OFFICE_ARCHIVE_URLS="xnds://pfconrg06.kek.jp:8080/archive/cgi/ArchiveDataServer.cgi xnds://pfconrg07.kek.jp:8082/archive/cgi/ArchiveDataServer.cgi"
CERL_OFFICE_ARCHIVE_NAMES_1="Archive"
CERL_OFFICE_ARCHIVE_NAMES_2="Archive"
CERL_OFFICE_COLOR_DEF="/Operation/cERL/color_cERL.def"
CERL_OFFICE_FONT_DEF="/Operation/cERL/font_cERL.def"
CERL_OFFICE_SHARE_LINK_SRC_WIN='\\pfrnas3.kek.jp\public\public\EPICS\CSS\Operation\cERL'
CERL_OFFICE_SHARE_LINK_SRC="/pf/epics/app/OP/CSS/OP-Workspace/cERL"
CERL_OFFICE_SHARE_LINK_DEST="/Operation/cERL"
