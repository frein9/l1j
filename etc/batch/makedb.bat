SET MYSQL=mysql
SET USER=root
SET PASSWORD=
%MYSQL%  --user=%USER% --password=%PASSWORD% --default-character-set=euckr < ..\..\db\makedb.sql
%MYSQL%  --user=%USER% --password=%PASSWORD% --default-character-set=euckr < ..\..\db\make_tables.sql
