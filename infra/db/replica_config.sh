if [ -z "$(ls -A /var/lib/postgresql/data)" ]; then
  until pg_basebackup -h service-db -D /var/lib/postgresql/data -U replicator -v -P --wal-method=stream
  do
    echo "Waiting for primary to connect..."
    sleep 1s
  done

  echo "Backup done, starting replica..."
  chmod 0700 /var/lib/postgresql/data

  echo "primary_conninfo = 'host=service-db port=5432 user=replicator password=replicator_password application_name=pgslave'" >> /var/lib/postgresql/data/postgresql.conf
  touch /var/lib/postgresql/data/standby.signal
fi

echo "start postgres"
postgres
