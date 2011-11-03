VALID_ACCS="JPARC PFAR PF LINAC SUPERKEKB JPARC_OFFICE PFAR_OFFICE PF_OFFICE LINAC_OFFICE SUPERKEKB_OFFICE"

JPARC_ADDR_LIST="jparc.kek.jp"
JPARC_ARCHIVE_URLS="jdbc:postgresql://jparc.kek.jp:5432/archive"
JPARC_ARCHIVE_NAMES_1="rdb"

PFAR_ADDR_LIST="pf-ar.kek.jp"
PFAR_ARCHIVE_URLS="xnds://pf-ar.kekjp/archive/cgi/ArchiveDataServer.cgi"
PFAR_ARCHIVE_NAMES_1="Archive"

PF_ADDR_LIST="pf.kek.jp"
PF_ARCHIVE_URLS="xnds://pf.kekjp/archive/cgi/ArchiveDataServer.cgi"
PF_ARCHIVE_NAMES_1="Archive"

LINAC_ADDR_LIST="172.19.95.255"
LINAC_ARCHIVE_URLS="xnds://lcba03.linac.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.linac.kek.jp:5432/archive"
LINAC_ARCHIVE_NAMES_1="Archive"
LINAC_ARCHIVE_NAMES_2="rdb"

SUPERKEKB_ADDR_LIST="172.19.63.255"
SUPERKEKB_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.

JPARC_OFFICE_ADDR_LIST="jparc-office.kek.jp"
JPARC_OFFICE_ARCHIVE_URLS="jdbc:postgresql://jparc-office.kek.jp:5432/archive"
JPARC_OFFICE_ARCHIVE_NAMES_1="rdb"

PFAR_OFFICE_ADDR_LIST="pf-ar-office.kek.jp"
PFAR_OFFICE_ARCHIVE_URLS="xnds://pf-ar-office.kekjp/archive/cgi/ArchiveDataServer.cgi"
PFAR_OFFICE_ARCHIVE_NAMES_1="Archive"

PF_OFFICE_ADDR_LIST="pf-office.kek.jp"
PF_OFFICE_ARCHIVE_URLS="xnds://pf-office.kekjp/archive/cgi/ArchiveDataServer.cgi"
PF_OFFICE_ARCHIVE_NAMES_1="Archive"

LINAC_OFFICE_ADDR_LIST="172.19.95.255"
LINAC_OFFICE_ARCHIVE_URLS="xnds://www-linac2.kek.jp:4080/RPC2 jdbc:postgresql://lcba09.kek.jp:5432/archive"
LINAC_OFFICE_ARCHIVE_NAMES_1="Archive" 
LINAC_OFFICE_ARCHIVE_NAMES_2="rdb"

SUPERKEKB_OFFICE_ADDR_LIST="172.19.63.255"
SUPERKEKB_OFFICE_ARCHIVE_URLS="kblog:///KEKBLog"
SUPERKEKB_OFFICE_ARCHIVE_NAMES_1=$(kblog_sub_archives ${SUPERKEKB_OFFICE_ARCHIVE_URLS})
 # Note that "source kblog_settings.sh" must be called beforehand.

