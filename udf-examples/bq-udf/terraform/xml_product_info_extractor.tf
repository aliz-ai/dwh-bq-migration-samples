locals {
  udf_name           = "xml_product_info_extractor"
  udf_entry_point    = "ai.aliz.bqmigration.samples.bq.XmlProductInfoExtractorUDF"
  udf_archive_bucket = "${var.gcp_project_id}-udf-archive"
  ddl_query = join(" ", [
    "CREATE OR REPLACE FUNCTION `${var.gcp_project_id}.${google_bigquery_dataset.udfs.dataset_id}.${local.udf_name}`",
    "(input STRING) RETURNS STRING",
    "REMOTE WITH CONNECTION `${var.gcp_project_id}.${var.location}.${google_bigquery_connection.connection.connection_id}`",
    "OPTIONS (endpoint = 'https://${var.location}-${var.gcp_project_id}.cloudfunctions.net/${local.udf_name}');"
  ])
}

resource "google_bigquery_connection" "connection" {
  connection_id = "bq_remote_udf"
  project       = var.gcp_project_id
  location      = var.location
  cloud_resource {}
}

resource "google_project_iam_member" "project" {
  project = var.gcp_project_id
  role    = "roles/run.invoker"
  member  = "serviceAccount:${google_bigquery_connection.connection.cloud_resource[0].service_account_id}"
}

resource "google_cloudfunctions2_function" "cloud_functions" {
  project     = var.gcp_project_id
  name        = local.udf_name
  description = "${local.udf_name}:${local.udf_entry_point} BQ remote function"
  location    = var.location

  build_config {
    runtime     = "java17"
    entry_point = local.udf_entry_point
    source {

      storage_source {
        bucket = local.udf_archive_bucket
        object = "functions.zip"
      }
    }
  }

  service_config {
    all_traffic_on_latest_revision   = true
    available_cpu                    = "1"
    available_memory                 = "256M"
    ingress_settings                 = "ALLOW_ALL"
    max_instance_count               = 1000
    max_instance_request_concurrency = 100
    min_instance_count               = 0
    timeout_seconds                  = 60
  }
}

resource "google_bigquery_job" "remote_udf_creation" {
  job_id   = "remote_udf_creation_bq_job_${local.udf_name}"
  project  = var.gcp_project_id
  location = var.location

  query {
    query              = local.ddl_query
    create_disposition = ""
    write_disposition  = ""
  }
  depends_on = [google_cloudfunctions2_function.cloud_functions, google_bigquery_dataset.udfs]

}