resource "google_bigquery_routine" "split_string_with_index_udf" {
  project         = var.gcp_project_id
  dataset_id      = google_bigquery_dataset.udfs.dataset_id
  routine_id      = "SPLIT_STRING_WITH_INDEX"
  routine_type    = "SCALAR_FUNCTION"
  language        = "SQL"
  definition_body = <<EOT
  ARRAY(
    SELECT AS STRUCT index, TRIM(part)
    FROM UNNEST(SPLIT(input, delimiter)) part WITH OFFSET AS index)
EOT
  description     = "Filters out empty or null values from an array and returns the filtered array."

  arguments {
    name      = "input"
    data_type = "{\"typeKind\" :  \"STRING\"}"
  }

  arguments {
    name      = "delimiter"
    data_type = "{\"typeKind\" :  \"STRING\"}"
  }
}