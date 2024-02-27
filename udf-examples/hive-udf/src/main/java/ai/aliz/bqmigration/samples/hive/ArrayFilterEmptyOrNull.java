package ai.aliz.bqmigration.samples.hive;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

@Description(name = "array_filter_empty_or_null", value = "_FUNC_(array) - ")
public class ArrayFilterEmptyOrNull extends GenericUDF {

    private ListObjectInspector inpuListObjectInspector = null;

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 1) {
            throw new UDFArgumentLengthException("array_filter_null only takes 1 argument with type array");
        }
        if (!(arguments[0] instanceof ListObjectInspector)) {
            throw new UDFArgumentException("array_filter_null only takes array as argument and not " + arguments[0].getTypeName());
        }
        inpuListObjectInspector = (ListObjectInspector) arguments[0];
        return inpuListObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        List<?> inputList = inpuListObjectInspector.getList(arguments[0].get());

        if (inputList == null) {
            return new ArrayList<>();
        }

        List<Object> returnList = inputList.stream()
                                            .filter(element -> element != null && !element.toString().isEmpty())
                                            .collect(Collectors.toList());
       
        return returnList;
    }

    @Override
    public String getDisplayString(String[] children) {
        return "array_filter_empty_or_null()";
    }

}
