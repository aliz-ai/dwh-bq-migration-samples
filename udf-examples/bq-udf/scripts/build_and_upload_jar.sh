gcp_project_id=$1

cd ../remote-functions/

mvn clean install

cd target/

zip functions.zip bq-udfs-1.0-SNAPSHOT-jar-with-dependencies.jar

bucket_name="${gcp_project_id}-udf-archive"

if gsutil ls "gs://$bucket_name"
then
    echo "Bucket gs://$bucket_name already exists."
else
    echo "Bucket gs://$bucket_name does not exist. Creating..."
    gsutil mb "gs://$bucket_name"
    echo "Bucket gs://$bucket_name created."
fi

gcloud storage cp functions.zip "gs://${bucket_name}/functions.zip"