package org.sunspec.model;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.sunspec.model.entities.Group;
import org.sunspec.model.entities.Point;
import org.sunspec.model.entities.SunSpecModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestModelStructure {

    @Test
    void checkModelId() {
        SunSpec.getModels().forEach((id, model) -> assertEquals(id, model.id, "Index vs model id mismatch"));
    }

    // --------------------------------------------------

    @Test
    void checkAllScalingFactorsExist() {
        SunSpec.getModels().values().forEach(this::checkAllScalingFactorsExist);
    }

    private void checkAllScalingFactorsExist(SunSpecModel sunSpecModel) {
        List<String> fixedBlockScalingFactorsNames = getScalingFactors(sunSpecModel.group.points);
        String modelLogName = String.format("Model %5d", sunSpecModel.id);

        // Check all points in the fixed model
        checkPointsScalingFactor(modelLogName, sunSpecModel.group.points, fixedBlockScalingFactorsNames);

        // Check all points in the repeating models model
        for (Group group : sunSpecModel.group.groups) {
            List<String> allScalingFactorsForThisBlock = new ArrayList<>();
            allScalingFactorsForThisBlock.addAll(fixedBlockScalingFactorsNames);
            allScalingFactorsForThisBlock.addAll(getScalingFactors(group.points));

            checkPointsScalingFactor(modelLogName, group.points, allScalingFactorsForThisBlock);
        }
    }

    private List<String> getScalingFactors(List<Point> points) {
        return points.stream()
            .filter(point -> point.type.equals(Point.Type.SUNSSF))
            .map(point -> point.name)
            .collect(Collectors.toList());
    }

    private void checkPointsScalingFactor(String modelLogName, List<Point> points, List<String> scalingFactorsNames) {
        points
            .stream()
            .filter(point -> !point.type.equals(Point.Type.SUNSSF))
            .filter(point -> point.scalingFactor != null && !point.scalingFactor.isEmpty())
            .forEach(point -> {
                try {
                    int scalingFactor = Integer.parseInt(point.scalingFactor);
                    assertTrue(scalingFactor >= -10 && scalingFactor <= 10,
                        modelLogName + " invalid numerical scaling factor");
                }
                catch (NumberFormatException nfe) {
                    assertTrue(scalingFactorsNames.contains(point.scalingFactor),
                        modelLogName + " no scaling factor " + point.scalingFactor + " exists.");
                }
            });
    }

    // --------------------------------------------------

    @Test
    void checkRegisterCountPerType() {
        SunSpec.getModels().values().forEach(this::checkRegisterCountPerType);
    }

    private void checkRegisterCountPerType(SunSpecModel sunSpecModel) {
        String modelLogName = String.format("Model %5d", sunSpecModel.id);
        checkRegisterCountPerType(modelLogName, sunSpecModel.group.points);
        for (Group group : sunSpecModel.group.groups) {
            checkRegisterCountPerType(modelLogName, group.points);
        }
    }

    private void checkRegisterCountPerType(String modelLogName, List<Point> points) {
        for (Point point : points) {
            Integer registerCount = point.type.getRegisterCount();
            if (registerCount == null) {
                assertEquals(Point.Type.STRING, point.type,
                    "Only a string may have a variable number of registers");
                assertNotNull(point.size, "All strings MUST have a size specified");
                assertTrue(point.size > 1, "Strings are expected to be a string " + point.size);
            } else {
                assertEquals(registerCount, point.size,
                    String.format("%s: Point with type/size mismatch: %s",
                        modelLogName, point));
            }
        }
    }

    // --------------------------------------------------

    @Test
    void checkSizesOfPointsInModel() {
        SunSpec.getModels().values().forEach(this::checkSizesOfPointsInModel);
    }

    private void checkSizesOfPointsInModel(SunSpecModel sunSpecModel) {
        Integer modelNr = sunSpecModel.id;
        ModelSize modelSize = modelSizes.get(modelNr);
        if (modelSize != null) {
            Integer registersInFixedBlock = sunSpecModel
                .group
                .points
                .stream()
                // Two fields that define the chain and are not part of the payload
                .filter(point -> !"ID".equals(point.name))
                .filter(point -> !"L".equals(point.name))
                .map(point -> point.size).reduce(0, Integer::sum);

            Integer registersInRepeatingBlock = sunSpecModel
                .group
                .groups
                .stream()
                .map(group -> group.points.stream()
                                .map(point -> point.size)
                                .reduce(0, Integer::sum))
                .reduce(0, Integer::sum);

            assertEquals(modelSize.fixedBlock, registersInFixedBlock,
                String.format("Model %d: Invalid fixed block size", sunSpecModel.id));

            assertEquals(modelSize.repeatingBlock, registersInRepeatingBlock,
                String.format("Model %d: Invalid repeating block size", sunSpecModel.id));

            assertEquals(modelSize.model, registersInFixedBlock + registersInRepeatingBlock,
                String.format("Model %d: Invalid total model size", sunSpecModel.id));
        }
    }

    @AllArgsConstructor
    private static class ModelSize{
        int model;
        int fixedBlock;
        int repeatingBlock;
    }

    // FIXME: Values obtained from the XML representation of the model.
    //        So not all models from the JSon are present
    private static final Map<Integer, ModelSize> modelSizes = new TreeMap<>();
    static {
        modelSizes.put(    1, new ModelSize(  66,  66,   0 ));
        modelSizes.put(    2, new ModelSize(  14,  14,   0 ));
        modelSizes.put(    3, new ModelSize(  59,  58,   1 ));
        modelSizes.put(    4, new ModelSize(  61,  60,   1 ));
        modelSizes.put(    5, new ModelSize(  89,  88,   1 ));
        modelSizes.put(    6, new ModelSize(  91,  90,   1 ));
        modelSizes.put(    7, new ModelSize(  11,  10,   1 ));
        modelSizes.put(    8, new ModelSize(   3,   2,   1 ));
        modelSizes.put(    9, new ModelSize(  93,  92,   1 ));
        modelSizes.put(   10, new ModelSize(   4,   4,   0 ));
        modelSizes.put(   11, new ModelSize(  13,  13,   0 ));
        modelSizes.put(   12, new ModelSize(  98,  98,   0 ));
        modelSizes.put(   13, new ModelSize( 174, 174,   0 ));
        modelSizes.put(   14, new ModelSize(  52,  52,   0 ));
        modelSizes.put(   15, new ModelSize(  24,  24,   0 ));
        modelSizes.put(   16, new ModelSize(  52,  52,   0 ));
        modelSizes.put(   17, new ModelSize(  12,  12,   0 ));
        modelSizes.put(   18, new ModelSize(  22,  22,   0 ));
        modelSizes.put(   19, new ModelSize(  30,  30,   0 ));
        modelSizes.put(  101, new ModelSize(  50,  50,   0 ));
        modelSizes.put(  102, new ModelSize(  50,  50,   0 ));
        modelSizes.put(  103, new ModelSize(  50,  50,   0 ));
        modelSizes.put(  111, new ModelSize(  60,  60,   0 ));
        modelSizes.put(  112, new ModelSize(  60,  60,   0 ));
        modelSizes.put(  113, new ModelSize(  60,  60,   0 ));
        modelSizes.put(  120, new ModelSize(  26,  26,   0 ));
        modelSizes.put(  121, new ModelSize(  30,  30,   0 ));
        modelSizes.put(  122, new ModelSize(  44,  44,   0 ));
        modelSizes.put(  123, new ModelSize(  24,  24,   0 ));
        modelSizes.put(  124, new ModelSize(  24,  24,   0 ));
        modelSizes.put(  125, new ModelSize(   8,   8,   0 ));
        modelSizes.put(  126, new ModelSize(  64,  10,  54 ));
        modelSizes.put(  127, new ModelSize(  10,  10,   0 ));
        modelSizes.put(  128, new ModelSize(  14,  14,   0 ));
        modelSizes.put(  129, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  130, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  131, new ModelSize(  64,  10,  54 ));
        modelSizes.put(  132, new ModelSize(  64,  10,  54 ));
        modelSizes.put(  133, new ModelSize(  66,   6,  60 ));
        modelSizes.put(  134, new ModelSize(  68,  10,  58 ));
        modelSizes.put(  135, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  136, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  137, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  138, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  139, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  140, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  141, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  142, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  143, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  144, new ModelSize(  60,  10,  50 ));
        modelSizes.put(  145, new ModelSize(   8,   8,   0 ));
        modelSizes.put(  160, new ModelSize(  28,   8,  20 ));
        modelSizes.put(  201, new ModelSize( 105, 105,   0 ));
        modelSizes.put(  202, new ModelSize( 105, 105,   0 ));
        modelSizes.put(  203, new ModelSize( 105, 105,   0 ));
        modelSizes.put(  204, new ModelSize( 105, 105,   0 ));
        modelSizes.put(  211, new ModelSize( 124, 124,   0 ));
        modelSizes.put(  212, new ModelSize( 124, 124,   0 ));
        modelSizes.put(  213, new ModelSize( 124, 124,   0 ));
        modelSizes.put(  214, new ModelSize( 124, 124,   0 ));
        modelSizes.put(  220, new ModelSize(  43,  42,   1 ));
        modelSizes.put(  302, new ModelSize(   5,   0,   5 ));
        modelSizes.put(  303, new ModelSize(   1,   0,   1 ));
        modelSizes.put(  304, new ModelSize(   6,   0,   6 ));
        modelSizes.put(  305, new ModelSize(  36,  36,   0 ));
        modelSizes.put(  306, new ModelSize(   4,   4,   0 ));
        modelSizes.put(  307, new ModelSize(  11,  11,   0 ));
        modelSizes.put(  308, new ModelSize(   4,   4,   0 ));
        modelSizes.put(  401, new ModelSize(  22,  14,   8 ));
        modelSizes.put(  402, new ModelSize(  34,  20,  14 ));
        modelSizes.put(  403, new ModelSize(  24,  16,   8 ));
        modelSizes.put(  404, new ModelSize(  39,  25,  14 ));
        modelSizes.put(  501, new ModelSize(  31,  31,   0 ));
        modelSizes.put(  502, new ModelSize(  28,  28,   0 ));
        modelSizes.put(  601, new ModelSize(  48,  26,  22 ));
        modelSizes.put(  801, new ModelSize(   1,   1,   0 ));
        modelSizes.put(  802, new ModelSize(  62,  62,   0 ));
        modelSizes.put(  803, new ModelSize(  58,  26,  32 ));
        modelSizes.put(  804, new ModelSize(  62,  46,  16 ));
        modelSizes.put(  805, new ModelSize(  46,  42,   4 ));
        modelSizes.put(  806, new ModelSize(   2,   1,   1 ));
        modelSizes.put(  807, new ModelSize(  58,  34,  24 ));
        modelSizes.put(  808, new ModelSize(   2,   1,   1 ));
        modelSizes.put(  809, new ModelSize(   2,   1,   1 ));
        modelSizes.put(63001, new ModelSize( 152, 134,  18 ));
        modelSizes.put(63002, new ModelSize(   4,   0,   4 ));
        modelSizes.put(64001, new ModelSize(  71,  71,   0 ));
        modelSizes.put(64020, new ModelSize(  46,  30,  16 ));
        modelSizes.put(64101, new ModelSize(   7,   7,   0 ));
        modelSizes.put(64110, new ModelSize( 282, 282,   0 ));
        modelSizes.put(64111, new ModelSize(  23,  23,   0 ));
        modelSizes.put(64112, new ModelSize(  64,  64,   0 ));
    }

}
