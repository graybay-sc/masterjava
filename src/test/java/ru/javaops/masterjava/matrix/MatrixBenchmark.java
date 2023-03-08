package ru.javaops.masterjava.matrix;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@Fork(value = 100)
@Fork(value = 1)
public class MatrixBenchmark {

    public int[][] matrixA;
    public int[][] matrixB;

    @Param({"1000"})
    public int matrixSize;

    public static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void singleThreadMultiply(Blackhole blackhole) {
        prepareMatrix();
        int[][] result = MatrixUtil.singleThreadMultiply(matrixA, matrixB);
        blackhole.consume(result);
        if (!Arrays.deepEquals(result, createExpectedMatrix(matrixA, matrixB))) {
            throw new IllegalArgumentException();
        }
    }

    @Benchmark
    public void singleThreadMultiplyOpt(Blackhole blackhole) {
        prepareMatrix();
        int[][] result = MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
        blackhole.consume(result);
        if (!Arrays.deepEquals(result, createExpectedMatrix(matrixA, matrixB))) {
            throw new IllegalArgumentException();
        }
    }

    @Benchmark
    public void singleThreadMultiplyOpt2(Blackhole blackhole) {
        prepareMatrix();
        int[][] result = MatrixUtil.singleThreadMultiplyOpt2(matrixA, matrixB);
        blackhole.consume(result);
        if (!Arrays.deepEquals(result, createExpectedMatrix(matrixA, matrixB))) {
            throw new IllegalArgumentException();
        }
    }


    @Benchmark
    public void concurrentMultiply(Blackhole blackhole) throws ExecutionException, InterruptedException {
        prepareMatrix();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        int[][] result;
        try {
            result = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            blackhole.consume(result);
        } finally {
            executor.shutdown();
        }
        if (!Arrays.deepEquals(result, createExpectedMatrix(matrixA, matrixB))) {
            throw new IllegalArgumentException();
        }
    }

    public int[][] generateMatrix() {
        int[][] matrix = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            Arrays.fill(matrix[i], 2);
        }
        return matrix;
    }

    private void prepareMatrix() {
        matrixA = generateMatrix();
        matrixB = generateMatrix();
    }

    private int[][] createExpectedMatrix(int[][] matrixA, int[][] matrixB) {
        double[][] matrixADouble = intMatrixToDoubleMatrix(matrixA);
        double[][] matrixBDouble = intMatrixToDoubleMatrix(matrixB);
        RealMatrix realMatrixA = MatrixUtils.createRealMatrix(matrixADouble);
        RealMatrix realMatrixB = MatrixUtils.createRealMatrix(matrixBDouble);
        return doubleMatrixToIntMatrix(realMatrixA.multiply(realMatrixB).getData());
    }

    private double[][] intMatrixToDoubleMatrix(int[][] intMatrix) {
        double[][] doubleMatrix = new double[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                doubleMatrix[i][j] = intMatrix[i][j];
            }
        }
        return doubleMatrix;
    }

    private int[][] doubleMatrixToIntMatrix(double[][] doubleMatrix) {
        int[][] intMatrix = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                intMatrix[i][j] = (int) doubleMatrix[i][j];
            }
        }
        return intMatrix;
    }
}