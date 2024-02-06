resource "google_bigquery_routine" "exact_median_sql_udf" {
  project         = var.gcp_project_id
  dataset_id      = google_bigquery_dataset.udfs.dataset_id
  routine_id      = "EXACT_MEDIAN"
  routine_type    = "SCALAR_FUNCTION"
  language        = "SQL"
  definition_body = <<EOT
CASE
    -- even number of elements
    WHEN MOD(ARRAY_LENGTH(arr), 2) = 0 THEN (arr[DIV(ARRAY_LENGTH(arr), 2) - 1] + arr[DIV(ARRAY_LENGTH(arr), 2)]) / 2
    -- odd number of elements
    ELSE arr[DIV(ARRAY_LENGTH(arr), 2)] 
  END
EOT
  description     = "Calculates the exact median"
  arguments {
    name          = "ARR"
    argument_kind = "ANY_TYPE"
  }
}
