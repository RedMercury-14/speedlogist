package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.List;


public class SumCombinatorUtils {

	public List<ResultPair> findCombinations(double target, List<Double> numbers, int maxElements) {
        List<ResultPair> result = new ArrayList<>();
        double totalSum = sum(numbers);

        // Если сумма всех элементов меньше целевого числа, просто добавляем все элементы как одну комбинацию
        if (totalSum < target) {
            result.add(new ResultPair(new ArrayList<>(numbers), new ArrayList<>())); // Все элементы использованы
            return result;
        }

        // Иначе ищем все возможные комбинации
        boolean[] used = new boolean[numbers.size()]; // Массив для отслеживания использованных элементов
        findCombinationsHelper(target, numbers, new ArrayList<>(), result, 0, maxElements, used);
        return result;
    }

    private void findCombinationsHelper(double remaining, List<Double> numbers, List<Double> current, 
                                               List<ResultPair> result, int start, int maxElements, boolean[] used) {
        if (remaining < 0) {
            return; // Останавливаем, если сумма больше, чем target
        }

        if (Math.abs(remaining) < 0.000001) { // Если мы близки к цели
            List<Double> usedList = new ArrayList<>(current);
            List<Double> unusedList = new ArrayList<>();
            
            // Добавляем неиспользованные элементы
            for (int i = 0; i < numbers.size(); i++) {
                if (!used[i]) {
                    unusedList.add(numbers.get(i));
                }
            }
            result.add(new ResultPair(usedList, unusedList));
            return;
        }

        if (current.size() >= maxElements) {
            return; // Останавливаем, если количество элементов в комбинации превышает лимит
        }

        for (int i = start; i < numbers.size(); i++) {
            if (i > start && numbers.get(i).equals(numbers.get(i - 1))) {
                continue; // Пропускаем дубликаты
            }
            current.add(numbers.get(i));
            used[i] = true; // Помечаем элемент как использованный
            findCombinationsHelper(remaining - numbers.get(i), numbers, current, result, i + 1, maxElements, used);
            used[i] = false; // Снимаем отметку с элемента
            current.remove(current.size() - 1);
        }
    }

    private  double sum(List<Double> numbers) {
        double total = 0;
        for (double number : numbers) {
            total += number;
        }
        return total;
    }

}
