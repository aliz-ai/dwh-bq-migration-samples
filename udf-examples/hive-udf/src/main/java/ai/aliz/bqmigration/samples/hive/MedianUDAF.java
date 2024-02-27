package ai.aliz.bqmigration.samples.hive;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFResolver2;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;


@Description(name = "median", value = "_FUNC_(x) - Returns the median of a set of numbers")
public class MedianUDAF implements GenericUDAFResolver2 {
    

    public static class MedianUDAFEvaluator extends GenericUDAFEvaluator {

        @Override
        public ObjectInspector init(Mode m, ObjectInspector[] parameters) throws HiveException {
            super.init(m, parameters);
            return PrimitiveObjectInspectorFactory.javaDoubleObjectInspector;
        }

        static class MedianAgg extends AbstractAggregationBuffer {
            private ArrayList<Double> container;
        }

        @Override
        public AggregationBuffer getNewAggregationBuffer() throws HiveException {
            MedianAgg result = new MedianAgg();
            reset(result);
            return result;
        }

        @Override
        public void reset(AggregationBuffer agg) throws HiveException {
            ((MedianAgg) agg).container = new ArrayList<Double>();
        }

        @Override
        public void iterate(AggregationBuffer agg, Object[] parameters) throws HiveException {
            assert (parameters.length == 1);
            Object p = parameters[0];
            if (p != null) {
                MedianAgg myagg = (MedianAgg) agg;
                myagg.container.add(((Double) p));
            }
        }

        @Override
        public Object terminatePartial(AggregationBuffer agg) throws HiveException {
            MedianAgg myagg = (MedianAgg) agg;
            return new ArrayList<Double>(myagg.container);
        }

        @Override
        public void merge(AggregationBuffer agg, Object partial) throws HiveException {
            MedianAgg myagg = (MedianAgg) agg;
            ArrayList<Double> partialResult = (ArrayList<Double>) partial;
            myagg.container.addAll(partialResult);
        }

        @Override
        public Double terminate(AggregationBuffer agg) throws HiveException {
            MedianAgg myagg = (MedianAgg) agg;
            int size = myagg.container.size();
            if (size == 0) {
                return null;
            } else {
                Collections.sort(myagg.container);
                if (size % 2 == 0) {
                    return (myagg.container.get((size - 1) / 2) + myagg.container.get(size / 2)) / 2.0;
                } else {
                    return myagg.container.get(size / 2);
                }
            }
        }
    }

    @Override
    public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters) throws SemanticException {
        return new MedianUDAFEvaluator();
    }

    @Override
    public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo info) throws SemanticException {
        return new MedianUDAFEvaluator();
    }
}