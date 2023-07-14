# from webinar
FROM postgres:13.7-alpine
COPY server/src/main/resources/schema.sql /docker-entrypoint-initdb.sql
EXPOSE 5432