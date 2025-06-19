package by.base.main.util.hcolossus.algorithm;

import java.util.Map;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

import by.base.main.model.Shop;

/**
 * контроля перемещений между кластерами.
 * Работает!
 * @author Dima
 */
public class ClusterConstraint implements HardActivityConstraint{
	
	private final Map<Location, Integer> jobClusterMap; // Location -> clusterId
    private final Shop depo;

    public ClusterConstraint(Map<Location, Integer> jobClusterMap, Shop shop) {
        this.jobClusterMap = jobClusterMap;
        this.depo = shop;
    }

    @Override
    public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, 
                                    TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
    	String depotName = depo.getNumshop() + "";

        // 1. Если newAct - склад, разрешаем
        if (newAct.getName().equals(depotName)) {
            return ConstraintsStatus.FULFILLED;
        }

        // 2. Получаем кластеры для всех точек
        Integer prevCluster = jobClusterMap.get(prevAct.getLocation());
        Integer newCluster = jobClusterMap.get(newAct.getLocation());
        Integer nextCluster = (nextAct != null) ? jobClusterMap.get(nextAct.getLocation()) : null;

        // 3. Проверка на склад (prevAct или nextAct)
        boolean prevIsDepot = prevAct.getName().equals(depotName);
        boolean nextIsDepot = (nextAct != null) && nextAct.getName().equals(depotName);

        // 4. Основные условия:
        // 4.1 Если nextAct существует и не склад, и его кластер отличается от newCluster - запрещаем
        if (nextAct != null && !nextIsDepot && nextCluster != null && !nextCluster.equals(newCluster)) {
            return ConstraintsStatus.NOT_FULFILLED;
        }

        // 4.2 Если prevAct не склад и его кластер отличается от newCluster - запрещаем
        if (!prevIsDepot && prevCluster != null && !prevCluster.equals(newCluster)) {
            return ConstraintsStatus.NOT_FULFILLED;
        }

        // 5. Все проверки пройдены - разрешаем
        return ConstraintsStatus.FULFILLED;
    }

}
