package org.ecp.backend.utils;

import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.entity.Record;
import org.ecp.backend.enums.ContractType;
import org.ecp.backend.enums.Volt;

import java.util.List;
import java.util.stream.IntStream;

public class CalculatorUtils {
    public static double calculateConsume(List<Record> records, int size) {
        return size == 1 ? records.get(0).getConsume() : records.get(size - 1).getConsume() - records.get(0).getConsume();
    }

    public static double calculateNormal(List<Record> records, int size) {
        return size == 1 ? records.get(0).getNormal() : records.get(size - 1).getNormal() - records.get(0).getNormal();
    }

    public static double calculateLow(List<Record> records, int size) {
        return size == 1 ? records.get(0).getLow() : records.get(size - 1).getLow() - records.get(0).getLow();
    }

    public static double calculateHigh(List<Record> records, int size) {
        return size == 1 ? records.get(0).getHigh() : records.get(size - 1).getHigh() - records.get(0).getHigh();
    }

    public static double[] getPrices(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }
    public static double calFamily(int houses, double consume, double[] prices) {
        int[] limits = IntStream.rangeClosed(1, 5)
                .map(i -> getLimit(i, houses))
                .toArray();
        double cost = 0.0;
        int tierIndex = 0;
        while (consume > 0 && tierIndex < limits.length - 1) {
            double tierConsumption = Math.min(consume, limits[tierIndex + 1] - limits[tierIndex]);
            cost += tierConsumption * prices[tierIndex];
            consume -= tierConsumption;
            tierIndex++;
        }
        return cost;
    }

    private static int getLimit(int i, int houses) {
        try {
            return CommonConstant.class.getField("LIMIT" + i).getInt(null) * Math.max(houses, 1);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


    public static double calComplexPublicGovEduMedic(double normal, double price) {
        return normal * price;
    }

    public static double calProduceBusiness(double normal, double low, double high, double[] prices) {
        double costLow = low * prices[0];
        double costNormal = normal * prices[1];
        double costHigh = high * prices[2];
        return costNormal + costLow + costHigh;
    }
}
