resource "google_bigquery_routine" "array_filter_empty_or_null_sql_udf" {
  project         = var.gcp_project_id
  dataset_id      = google_bigquery_dataset.udfs.dataset_id
  routine_id      = "ARRAY_FILTER_EMPTY_OR_NULL"
  routine_type    = "SCALAR_FUNCTION"
  language        = "SQL"
  definition_body = <<EOT
ARRAY(
    SELECT e
    FROM UNNEST(INPUT_ARRAY) AS e
    WHERE e IS NOT NULL AND CAST(e AS STRING) != '')
EOT
  description     = "Filters out empty or null values from an array and returns the filtered array."

  arguments {
    name          = "INPUT_ARRAY"
    argument_kind = "ANY_TYPE"
  }

  depends_on = [google_bigquery_dataset.udfs]

}