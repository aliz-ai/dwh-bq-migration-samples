resource "google_bigquery_dataset" "udfs" {
    project = var.gcp_project_id
    dataset_id = "hive_sample_udfs"
    location = "US"
}

