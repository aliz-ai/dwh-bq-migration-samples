# Hive UDF, UDAF, UDTF migration samples

## Description

This module contains samples of Hive UDFs and their migrated BigQuery counterparts to demonstrate different migration approaches.

## Usage
### Hive UDFs
To build the Hive UDF java code
1. Go to the `hive-udf` folder
2. Execute the `mvn clean install` command

### BigQuery UDFs
To deploy the sample BigQuery UDFs
1. Go to the `bq-udf/terraform` folder
2. Execute `terraform init` command
3. Execute `terraform apply` command. The command will prompt for a GCP project id, input the id of the project where you wanna deploy the functions


## Samples

### ARRAY_FILTER_EMPTY_OR_NULL

- Approach: Reimplementation with a SQL UDF
- Hive UDF: [ArrayFilterEmptyOrNull.java](hive-udf/src/main/java/ai/aliz/bqmigration/samples/hive/ArrayFilterEmptyOrNull.java)
- BQ UDF: [array_filter_empty_or_null.tf](bq-udf/terraform/array_filter_empty_or_null.tf)
- Test query: 

```SELECT `hive_sample_udfs.ARRAY_FILTER_EMPTY_OR_NULL`(["ALMA", NULL, "", "KORTE"]);```

## Contributing

Contributions are welcome! If you have any improvements or bug fixes, feel free to submit a pull request.

## License

This module is licensed under the [MIT License](LICENSE).
