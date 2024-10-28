package by.base.main.util.hcolossus.service;

import java.util.List;

// Класс для хранения комбинации и неиспользованных элементов
class ResultPair {
    private List<Double> used;
    List<Double> unused;

    public ResultPair(List<Double> used, List<Double> unused) {
        this.used = used;
        this.unused = unused;
    }

    public List<Double> getUsed() {
        return used;
    }

    public List<Double> getUnused() {
        return unused;
    }
}