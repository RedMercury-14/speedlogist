package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SumCombinations {
	
//    public static void main(String[] args) {
//    	Date t1 = new Date();
//        double target = 8;
////        double[] numbers = {2, 1, 1, 2};  // Пример массива с общей суммой, меньшей чем target
//        double[] numbers = {1, 2, 5, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
//
//        List<List<Double>> combinations = findCombinations(target, numbers);
//        Date t2 = new Date();
//        
//        System.out.println("Возможные комбинации:");
//        for (List<Double> combination : combinations) {
//            System.out.println(combination);
//        }
//        System.out.println("Всего комбинаций: " + combinations.size());
//        System.out.println("Время выполнения: " + (t2.getTime()-t1.getTime()) + " ms");
//    }
//
//    public static List<List<Double>> findCombinations(double target, double[] numbers) {
//        List<List<Double>> result = new ArrayList<>();
//        double totalSum = sum(numbers);
//
//        // Если сумма всех элементов меньше целевого числа, просто добавляем все элементы как одну комбинацию
//        if (totalSum < target) {
//            List<Double> allElements = new ArrayList<>();
//            for (double number : numbers) {
//                allElements.add(number);
//            }
//            result.add(allElements);
//            return result; // Возвращаем комбинацию всех чисел
//        }
//
//        // Иначе ищем все возможные комбинации
//        findCombinationsHelper(target, numbers, new ArrayList<>(), result, 0);
//        return result;
//    }
//
//    private static void findCombinationsHelper(double remaining, double[] numbers, List<Double> current, List<List<Double>> result, int start) {
//        if (remaining < 0) {
//            return; // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            result.add(new ArrayList<>(current));
//            return;
//        }
//
//        for (int i = start; i < numbers.length; i++) {
//            if (i > start && numbers[i] == numbers[i - 1]) {
//                continue; // Пропускаем дубликаты
//            }
//            current.add(numbers[i]);
//            findCombinationsHelper(remaining - numbers[i], numbers, current, result, i + 1);
//            current.remove(current.size() - 1);
//        }
//    }
//
//    // Вспомогательный метод для нахождения суммы всех элементов массива
//    private static double sum(double[] numbers) {
//        double total = 0;
//        for (double number : numbers) {
//            total += number;
//        }
//        return total;
//    }
   
//    public static void main(String[] args) {
//        Date t1 = new Date();
//        double[] targets = {4, 4, 5}; // Массив целевых значений (машин)
////        double[] numbers = {1, 2, 2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
//        double[] numbers = {1, 2, 1, 1, 2, 1,2,3};
//
//        List<List<List<Double>>> allCombinations = findCombinations(targets, numbers);
//        Date t2 = new Date();
//
//        System.out.println("Возможные комбинации:");
//        for (List<List<Double>> combination : allCombinations) {
//            System.out.println(combination);
//        }
//        System.out.println("Всего комбинаций: " + allCombinations.size());
//        System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//    }
//
//    public static List<List<List<Double>>> findCombinations(double[] targets, double[] numbers) {
//        List<List<List<Double>>> result = new ArrayList<>();
//        findCombinationsHelper(targets, numbers, new ArrayList<>(), result, new boolean[numbers.length], 0);
//        return result;
//    }
//
//    private static void findCombinationsHelper(double[] targets, double[] numbers, List<List<Double>> current, List<List<List<Double>>> result, boolean[] used, int targetIndex) {
//        // Если все цели выполнены, добавляем текущее распределение в результат
//        if (targetIndex == targets.length) {
//            result.add(new ArrayList<>(current));
//            return;
//        }
//
//        double target = targets[targetIndex];
//        List<Double> currentCombination = new ArrayList<>();
//
//        // Находим комбинации для текущей цели
//        List<List<Double>> combinationsForTarget = findCombinationsForTarget(target, numbers, used, currentCombination, 0);
//
//        // Добавляем найденные комбинации в текущее распределение
//        for (List<Double> combination : combinationsForTarget) {
//            current.add(combination);
//            markUsed(combination, numbers, used); // Устанавливаем использованные числа
//            findCombinationsHelper(targets, numbers, current, result, used, targetIndex + 1);
//            current.remove(current.size() - 1); // Удаляем последнюю добавленную комбинацию
//            unmarkUsed(combination, numbers, used); // Сбрасываем использованные числа
//        }
//    }
//
//    private static List<List<Double>> findCombinationsForTarget(double remaining, double[] numbers, boolean[] used, List<Double> current, int start) {
//        List<List<Double>> result = new ArrayList<>();
//
//        if (remaining < 0) {
//            return result; // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            result.add(new ArrayList<>(current));
//            return result;
//        }
//
//        for (int i = start; i < numbers.length; i++) {
//            if (used[i] || (i > start && numbers[i] == numbers[i - 1])) {
//                continue; // Пропускаем использованные числа и дубликаты
//            }
//
//            current.add(numbers[i]);
//            used[i] = true;
//            result.addAll(findCombinationsForTarget(remaining - numbers[i], numbers, used, current, i + 1));
//            current.remove(current.size() - 1);
//            used[i] = false;
//        }
//
//        return result;
//    }
//
//    private static void markUsed(List<Double> combination, double[] numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.length; i++) {
//                if (num == numbers[i]) { // Используем правильную переменную numbers
//                    used[i] = true;
//                    break;
//                }
//            }
//        }
//    }
//
//    private static void unmarkUsed(List<Double> combination, double[] numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.length; i++) {
//                if (num == numbers[i]) {
//                    used[i] = false;
//                    break;
//                }
//            }
//        }
//    }
	
	//без дубликатов!
//    public static void main(String[] args) {
//        Date t1 = new Date();
//        double[] targets = {4, 4, 5}; // Массив целевых значений (машин)
//        double[] numbers = {1, 2, 1, 1, 2, 1, 2, 3};
//
//        List<List<List<Double>>> allCombinations = findCombinations(targets, numbers);
//        Date t2 = new Date();
//
//        System.out.println("Возможные комбинации:");
//        for (List<List<Double>> combination : allCombinations) {
//            System.out.println(combination);
//        }
//        System.out.println("Всего комбинаций: " + allCombinations.size());
//        System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//    }
//
//    public static List<List<List<Double>>> findCombinations(double[] targets, double[] numbers) {
//        List<List<List<Double>>> result = new ArrayList<>();
//        findCombinationsHelper(targets, numbers, new ArrayList<>(), result, new boolean[numbers.length], 0);
//        return result;
//    }
//
//    private static void findCombinationsHelper(double[] targets, double[] numbers, List<List<Double>> current, List<List<List<Double>>> result, boolean[] used, int targetIndex) {
//        // Если все цели выполнены, добавляем текущее распределение в результат
//        if (targetIndex == targets.length) {
//            result.add(new ArrayList<>(current));
//            return;
//        }
//
//        double target = targets[targetIndex];
//
//        // Находим комбинации для текущей цели
//        List<List<Double>> combinationsForTarget = findCombinationsForTarget(target, numbers, used, new ArrayList<>(), 0);
//
//        // Добавляем найденные комбинации в текущее распределение
//        for (List<Double> combination : combinationsForTarget) {
//            current.add(combination);
//            markUsed(combination, numbers, used); // Устанавливаем использованные числа
//            findCombinationsHelper(targets, numbers, current, result, used, targetIndex + 1);
//            current.remove(current.size() - 1); // Удаляем последнюю добавленную комбинацию
//            unmarkUsed(combination, numbers, used); // Сбрасываем использованные числа
//        }
//    }
//
//    private static List<List<Double>> findCombinationsForTarget(double remaining, double[] numbers, boolean[] used, List<Double> current, int start) {
//        Set<List<Double>> result = new HashSet<>(); // Используем Set для уникальных комбинаций
//
//        if (remaining < 0) {
//            return new ArrayList<>(); // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            Collections.sort(current); // Сортируем, чтобы избежать дубликатов
//            result.add(new ArrayList<>(current));
//            return new ArrayList<>(result); // Возвращаем уникальные комбинации
//        }
//
//        for (int i = start; i < numbers.length; i++) {
//            if (used[i] || (i > start && numbers[i] == numbers[i - 1])) {
//                continue; // Пропускаем использованные числа и дубликаты
//            }
//
//            current.add(numbers[i]);
//            used[i] = true;
//            result.addAll(findCombinationsForTarget(remaining - numbers[i], numbers, used, current, i + 1));
//            current.remove(current.size() - 1);
//            used[i] = false;
//        }
//
//        return new ArrayList<>(result); // Возвращаем уникальные комбинации
//    }
//
//    private static void markUsed(List<Double> combination, double[] numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.length; i++) {
//                if (num == numbers[i]) {
//                    used[i] = true;
//                    break;
//                }
//            }
//        }
//    }
//
//    private static void unmarkUsed(List<Double> combination, double[] numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.length; i++) {
//                if (num == numbers[i]) {
//                    used[i] = false;
//                    break;
//                }
//            }
//        }
//    }
	
	
//	//БЕЗ НЕ ДУБЛИКАТОВ РАБОЧИЙ
//    public static void main(String[] args) {
//        Date t1 = new Date();
//        List<Double> targets = Arrays.asList(4.0, 5.0, 10.0); // Список целевых значений (машин)
//        List<Double> numbers = Arrays.asList(1.0, 2.0, 1.0, 1.0, 2.0, 1.0, 2.0, 3.0); // Пример с недостаточным количеством паллет
//
//        List<List<List<Double>>> allCombinations = findCombinations(targets, numbers);
//        Date t2 = new Date();
//
//        System.out.println("Возможные комбинации:");
//        for (List<List<Double>> combination : allCombinations) {
//            System.out.println(combination);
//        }
//        System.out.println("Всего комбинаций: " + allCombinations.size());
//        System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//    }
//
//    public static List<List<List<Double>>> findCombinations(List<Double> targets, List<Double> numbers) {
//        List<List<List<Double>>> result = new ArrayList<>();
//        boolean[] used = new boolean[numbers.size()];
//        findCombinationsHelper(targets, numbers, new ArrayList<>(), result, used, 0);
//        return result;
//    }
//
//    private static void findCombinationsHelper(List<Double> targets, List<Double> numbers, List<List<Double>> current, List<List<List<Double>>> result, boolean[] used, int targetIndex) {
//        // Если все цели выполнены, добавляем текущее распределение в результат
//        if (targetIndex == targets.size()) {
//            result.add(new ArrayList<>(current));
//            return;
//        }
//
//        double target = targets.get(targetIndex);
//
//        // Находим комбинации для текущей цели
//        List<List<Double>> combinationsForTarget = findCombinationsForTarget(target, numbers, used, new ArrayList<>(), 0);
//
//        // Добавляем найденные комбинации в текущее распределение
//        for (List<Double> combination : combinationsForTarget) {
//            current.add(combination);
//            markUsed(combination, numbers, used); // Устанавливаем использованные числа
//            findCombinationsHelper(targets, numbers, current, result, used, targetIndex + 1);
//            current.remove(current.size() - 1); // Удаляем последнюю добавленную комбинацию
//            unmarkUsed(combination, numbers, used); // Сбрасываем использованные числа
//        }
//
//        // Если не нашли ни одной комбинации, добавляем пустую комбинацию для текущей цели
//        if (combinationsForTarget.isEmpty()) {
//            current.add(new ArrayList<>()); // Пустая комбинация для этой цели
//            findCombinationsHelper(targets, numbers, current, result, used, targetIndex + 1);
//            current.remove(current.size() - 1); // Удаляем пустую комбинацию
//        }
//    }
//
//    private static List<List<Double>> findCombinationsForTarget(double remaining, List<Double> numbers, boolean[] used, List<Double> current, int start) {
//        Set<List<Double>> result = new HashSet<>(); // Используем Set для уникальных комбинаций
//
//        if (remaining < 0) {
//            return new ArrayList<>(); // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            Collections.sort(current); // Сортируем, чтобы избежать дубликатов
//            result.add(new ArrayList<>(current));
//            return new ArrayList<>(result); // Возвращаем уникальные комбинации
//        }
//
//        for (int i = start; i < numbers.size(); i++) {
//            if (used[i] || (i > start && numbers.get(i).equals(numbers.get(i - 1)))) {
//                continue; // Пропускаем использованные числа и дубликаты
//            }
//
//            current.add(numbers.get(i));
//            used[i] = true;
//            result.addAll(findCombinationsForTarget(remaining - numbers.get(i), numbers, used, current, i + 1));
//            current.remove(current.size() - 1);
//            used[i] = false;
//        }
//
//        return new ArrayList<>(result); // Возвращаем уникальные комбинации
//    }
//
//    private static void markUsed(List<Double> combination, List<Double> numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.size(); i++) {
//                if (num == numbers.get(i)) {
//                    used[i] = true;
//                    break;
//                }
//            }
//        }
//    }
//
//    private static void unmarkUsed(List<Double> combination, List<Double> numbers, boolean[] used) {
//        for (double num : combination) {
//            for (int i = 0; i < numbers.size(); i++) {
//                if (num == numbers.get(i)) {
//                    used[i] = false;
//                    break;
//                }
//            }
//        }
//    }
	
	
	/**
	 * НОВЫЙ СПОСОБ!!!! ЦЕПОЧКА ОТВЕСТВЕННОСТИ!
	 * @param args
	 */
	
//	public static void main(String[] args) {
//	    Date t1 = new Date();
//	    double car = 8;
//	    double[] pall = {1, 2, 5, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
//
//	    int maxElements = 10; // Ограничение на количество элементов в комбинации
//	    List<List<Double>> combinations = findCombinations(car, pall, maxElements);
//	    Date t2 = new Date();
//	    
//	    System.out.println("Возможные комбинации:");
//	    for (List<Double> combination : combinations) {
//	        System.out.println(combination);
//	    }
//	    System.out.println("Всего комбинаций: " + combinations.size());
//	    System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//	}
//
//	public static List<List<Double>> findCombinations(double target, double[] numbers, int maxElements) {
//	    List<List<Double>> result = new ArrayList<>();
//	    double totalSum = sum(numbers);
//
//	    // Если сумма всех элементов меньше целевого числа, просто добавляем все элементы как одну комбинацию
//	    if (totalSum < target) {
//	        List<Double> allElements = new ArrayList<>();
//	        for (double number : numbers) {
//	            allElements.add(number);
//	        }
//	        result.add(allElements);
//	        return result; // Возвращаем комбинацию всех чисел
//	    }
//
//	    // Иначе ищем все возможные комбинации
//	    findCombinationsHelper(target, numbers, new ArrayList<>(), result, 0, maxElements);
//	    return result;
//	}
//
//	private static void findCombinationsHelper(double remaining, double[] numbers, List<Double> current, List<List<Double>> result, int start, int maxElements) {
//	    if (remaining < 0) {
//	        return; // Останавливаем, если сумма больше, чем target
//	    }
//
//	    if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//	        result.add(new ArrayList<>(current));
//	        return;
//	    }
//
//	    if (current.size() >= maxElements) {
//	        return; // Останавливаем, если количество элементов в комбинации превышает лимит
//	    }
//
//	    for (int i = start; i < numbers.length; i++) {
//	        if (i > start && numbers[i] == numbers[i - 1]) {
//	            continue; // Пропускаем дубликаты
//	        }
//	        current.add(numbers[i]);
//	        findCombinationsHelper(remaining - numbers[i], numbers, current, result, i + 1, maxElements);
//	        current.remove(current.size() - 1);
//	    }
//	}
//
//	// Вспомогательный метод для нахождения суммы всех элементов массива
//	private static double sum(double[] numbers) {
//	    double total = 0;
//	    for (double number : numbers) {
//	        total += number;
//	    }
//	    return total;
//	}
	
	
	//возвращает несипользованные
	//работает хорошо!
	//Используем как базу!
//	public static void main(String[] args) {
//        Date t1 = new Date();
//        double car = 8;
//        double[] pall = {1, 2, 5, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
//
//        int maxElements = 4; // Ограничение на количество элементов в комбинации
//        List<ResultPair> combinations = findCombinations(car, pall, maxElements);
//        Date t2 = new Date();
//        
//        System.out.println("Возможные комбинации:");
//        for (ResultPair combination : combinations) {
//            System.out.print("Комбинация: " + combination.getUsed());
//            System.err.println(" / " + combination.getUnused());
//        }
//        System.out.println("Всего комбинаций: " + combinations.size());
//        System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//    }
//
//    public static List<ResultPair> findCombinations(double target, double[] numbers, int maxElements) {
//        List<ResultPair> result = new ArrayList<>();
//        double totalSum = sum(numbers);
//
//        // Если сумма всех элементов меньше целевого числа, просто добавляем все элементы как одну комбинацию
//        if (totalSum < target) {
//            List<Double> allElements = new ArrayList<>();
//            for (double number : numbers) {
//                allElements.add(number);
//            }
//            result.add(new ResultPair(allElements, new ArrayList<>())); // Все элементы использованы, не осталось
//            return result;
//        }
//
//        // Иначе ищем все возможные комбинации
//        boolean[] used = new boolean[numbers.length]; // Массив для отслеживания использованных элементов
//        findCombinationsHelper(target, numbers, new ArrayList<>(), result, 0, maxElements, used);
//        return result;
//    }
//
//    private static void findCombinationsHelper(double remaining, double[] numbers, List<Double> current, List<ResultPair> result, int start, int maxElements, boolean[] used) {
//        if (remaining < 0) {
//            return; // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            List<Double> usedList = new ArrayList<>(current);
//            List<Double> unusedList = new ArrayList<>();
//            
//            // Добавляем неиспользованные элементы
//            for (int i = 0; i < numbers.length; i++) {
//                if (!used[i]) { // Проверяем, если элемент не использован
//                    unusedList.add(numbers[i]);
//                }
//            }
//            result.add(new ResultPair(usedList, unusedList));
//            return;
//        }
//
//        if (current.size() >= maxElements) {
//            return; // Останавливаем, если количество элементов в комбинации превышает лимит
//        }
//
//        for (int i = start; i < numbers.length; i++) {
//            if (i > start && numbers[i] == numbers[i - 1]) {
//                continue; // Пропускаем дубликаты
//            }
//            current.add(numbers[i]);
//            used[i] = true; // Помечаем элемент как использованный
//            findCombinationsHelper(remaining - numbers[i], numbers, current, result, i + 1, maxElements, used);
//            used[i] = false; // Снимаем отметку с элемента
//            current.remove(current.size() - 1);
//        }
//    }
//
//    // Вспомогательный метод для нахождения суммы всех элементов массива
//    private static double sum(double[] numbers) {
//        double total = 0;
//        for (double number : numbers) {
//            total += number;
//        }
//        return total;
//    }
//
//    // Класс для хранения комбинации и неиспользованных элементов
//    static class ResultPair {
//        private List<Double> used;
//        private List<Double> unused;
//
//        public ResultPair(List<Double> used, List<Double> unused) {
//            this.used = used;
//            this.unused = unused;
//        }
//
//        public List<Double> getUsed() {
//            return used;
//        }
//
//        public List<Double> getUnused() {
//            return unused;
//        }
//    }
	
	
	//резерв. Распределяет правильно, не не последовательно.
//    public static void main(String[] args) {
//        Date t1 = new Date();
//        List<Double> car = Arrays.asList(8.0, 4.0); // Пример списка машин
//        List<Double> pall = Arrays.asList(1.0, 2.0, 5.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 
//                                          0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 
//                                          0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
//        
//        int maxElements = 4; // Ограничение на количество элементов в комбинации
//        for (Double carCapacity : car) { // Перебираем каждую машину
//            List<ResultPair> combinations = findCombinations(carCapacity, pall, maxElements);
//            System.out.println("Возможные комбинации для машины с вместимостью " + carCapacity + ":");
//            for (ResultPair combination : combinations) {
//                System.out.print("Комбинация: " + combination.getUsed());
//                System.err.println(" / " + combination.getUnused());
//            }
//        }
//
//        Date t2 = new Date();
//        System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//    }
//
//    public static List<ResultPair> findCombinations(double target, List<Double> numbers, int maxElements) {
//        List<ResultPair> result = new ArrayList<>();
//        double totalSum = sum(numbers);
//
//        // Если сумма всех элементов меньше целевого числа, просто добавляем все элементы как одну комбинацию
//        if (totalSum < target) {
//            result.add(new ResultPair(new ArrayList<>(numbers), new ArrayList<>())); // Все элементы использованы
//            return result;
//        }
//
//        // Иначе ищем все возможные комбинации
//        boolean[] used = new boolean[numbers.size()]; // Массив для отслеживания использованных элементов
//        findCombinationsHelper(target, numbers, new ArrayList<>(), result, 0, maxElements, used);
//        return result;
//    }
//
//    private static void findCombinationsHelper(double remaining, List<Double> numbers, List<Double> current, 
//                                               List<ResultPair> result, int start, int maxElements, boolean[] used) {
//        if (remaining < 0) {
//            return; // Останавливаем, если сумма больше, чем target
//        }
//
//        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
//            List<Double> usedList = new ArrayList<>(current);
//            List<Double> unusedList = new ArrayList<>();
//            
//            // Добавляем неиспользованные элементы
//            for (int i = 0; i < numbers.size(); i++) {
//                if (!used[i]) {
//                    unusedList.add(numbers.get(i));
//                }
//            }
//            result.add(new ResultPair(usedList, unusedList));
//            return;
//        }
//
//        if (current.size() >= maxElements) {
//            return; // Останавливаем, если количество элементов в комбинации превышает лимит
//        }
//
//        for (int i = start; i < numbers.size(); i++) {
//            if (i > start && numbers.get(i).equals(numbers.get(i - 1))) {
//                continue; // Пропускаем дубликаты
//            }
//            current.add(numbers.get(i));
//            used[i] = true; // Помечаем элемент как использованный
//            findCombinationsHelper(remaining - numbers.get(i), numbers, current, result, i + 1, maxElements, used);
//            used[i] = false; // Снимаем отметку с элемента
//            current.remove(current.size() - 1);
//        }
//    }
//
//    private static double sum(List<Double> numbers) {
//        double total = 0;
//        for (double number : numbers) {
//            total += number;
//        }
//        return total;
//    }
//
//    // Класс для хранения комбинации и неиспользованных элементов
//    static class ResultPair {
//        private List<Double> used;
//        private List<Double> unused;
//
//        public ResultPair(List<Double> used, List<Double> unused) {
//            this.used = used;
//            this.unused = unused;
//        }
//
//        public List<Double> getUsed() {
//            return used;
//        }
//
//        public List<Double> getUnused() {
//            return unused;
//        }
//    }
	
	
	
//	public static void main(String[] args) {
//	    Date t1 = new Date();
//	    List<Double> car = Arrays.asList(8.0, 5.0); // Пример списка машин
//	    List<Double> pall = new ArrayList<>(Arrays.asList(1.0, 2.0, 5.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 
//	                                          0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 
//	                                          0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5));
//	    
//	    SumCombinatorUtils combinatorUtils = new SumCombinatorUtils();
//	    
//	    int maxElements = 10; // Ограничение на количество элементов в комбинации
//	    List<ResultPair> combinations = combinatorUtils.findCombinations(car.get(0), pall, maxElements); //1 контур
//    	System.out.println("ОТВЕТ; для распределения " + pall.size() + " магазинов");
//	    for (int i = 0; i < combinations.size(); i++) {
//	    	List<ResultPair> combinations2 = combinatorUtils.findCombinations(car.get(1), combinations.get(i).unused, maxElements); //2 контур
//	    	System.out.print("Комбинация для "+ car.get(0) + "("+combinations.get(i).unused.size()+")"+": " + combinations.get(i).getUsed() );
//            System.err.println(" / " + combinations.get(i).getUnused());
//	        for (ResultPair combination : combinations2) {
//	            System.out.print("  Комбинация для "+ car.get(1) + "("+combination.unused.size()+")"+": " + combination.getUsed());
//	            System.err.println(" / " + combination.getUnused());
//	        }
//		}
//	    
//
//	    Date t2 = new Date();
//	    System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
//	}

	public static void main(String[] args) {
	    Date t1 = new Date();
	    List<Double> car = Arrays.asList(8.0, 8.0); // Пример списка машин (любое количество)
//	    List<Double> pall = new ArrayList<>(Arrays.asList(1.0, 2.0, 5.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 
//	                                      0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 0.25, 
//	                                      0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5));
	    List<Double> pall = new ArrayList<>();
	    
	    String str3 = "1\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,5\r\n"
	    		+ "0,5\r\n"
	    		+ "1\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "1\r\n"
	    		+ "0,5\r\n"
	    		+ "0,5\r\n"
	    		+ "1\r\n"
	    		+ "2\r\n"
	    		+ "0,5\r\n"
	    		+ "0,5\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "1\r\n"
	    		+ "2\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "3\r\n"
	    		+ "0,5\r\n"
	    		+ "0,5\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,25\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "0,3\r\n"
	    		+ "3\r\n"
	    		+ "3\r\n"
	    		+ "5\r\n";
		 for (String string : str3.split("\r\n")) {
			 pall.add(Double.parseDouble(string.trim().replaceAll(",", ".")));
		}

	    SumCombinatorUtils combinatorUtils = new SumCombinatorUtils();
	    int maxElements = 10; // Ограничение на количество элементов в комбинации
	    
	    // Начинаем рекурсивное распределение
	    distributePallets(0, car, pall, combinatorUtils, maxElements); // Запускаем с первой машины

	    Date t2 = new Date();
	    System.out.println("Время выполнения: " + (t2.getTime() - t1.getTime()) + " ms");
	}

	// Рекурсивный метод для распределения паллет между машинами
	public static void distributePallets(int carIndex, List<Double> car, List<Double> pall, SumCombinatorUtils combinatorUtils, int maxElements) {
	    // Если все машины обработаны, выходим из рекурсии
	    if (carIndex >= car.size()) {
	        return;
	    }

	    // Находим комбинации для текущей машины
	    List<ResultPair> combinations = combinatorUtils.findCombinations(car.get(carIndex), pall, maxElements);
	    System.out.println("ОТВЕТ для машины с вместимостью " + car.get(carIndex) + " (" + pall.size() + " магазинов)");

	    // Для каждой комбинации текущей машины продолжаем распределение для следующих машин
	    for (ResultPair combination : combinations) {
	        System.out.print("Комбинация для машины " + car.get(carIndex) + " (" + combination.getUnused().size() + " осталось): " + combination.getUsed());
	        System.err.println(" / " + combination.getUnused());

	        // Рекурсивно вызываем распределение для следующей машины
	        distributePallets(carIndex + 1, car, combination.getUnused(), combinatorUtils, maxElements);
	    }
	}


	
}
