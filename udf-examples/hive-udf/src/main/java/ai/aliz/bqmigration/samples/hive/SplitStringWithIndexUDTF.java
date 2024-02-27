package ai.aliz.bqmigration.samples.hive;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;

public class SplitStringWithIndexUDTF extends GenericUDTF {

    private PrimitiveObjectInspector stringOI = null;
    private PrimitiveObjectInspector delimiterOI = null;

    @Override
    public StructObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
        if (args.length != 2) {
            throw new UDFArgumentException("SplitStringWithIndexUDTF() takes exactly two arguments");
        }

        stringOI = (PrimitiveObjectInspector) args[0];
        delimiterOI = (PrimitiveObjectInspector) args[1];

        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
        fieldNames.add("index");
        fieldNames.add("part");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] record) throws HiveException {
        final String inputString = PrimitiveObjectInspectorUtils.getString(record[0], stringOI);
        final String delimiter = PrimitiveObjectInspectorUtils.getString(record[1], delimiterOI);

        if (inputString == null || delimiter == null) {
            return;
        }

        String[] parts = inputString.split(delimiter);
        for (int i = 0; i < parts.length; i++) {
            forward(new Object[] {i, parts[i]});
        }
    }

    @Override
    public void close() throws HiveException {
        // Nothing to do
    }
}
