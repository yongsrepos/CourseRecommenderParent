package se.uu.it.cs.recsys.constraint.constraints;

import java.util.Map;
import java.util.Set;
import org.jacop.core.Store;
import org.jacop.set.constraints.EinA;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class FixedCourseSelectionConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedCourseSelectionConstraint.class);

    public static void impose(Store store,
            SetVar[] periodVars,
            Map<Integer, Set<Integer>> periodNumAndFixedIdSet) {

        LOGGER.debug("Posting constraints on fixed selection!");

        periodNumAndFixedIdSet.entrySet().forEach(
                (Map.Entry<Integer, Set<Integer>> entry) -> {

                    SetVar periodVar = periodVars[entry.getKey() - 1];

                    entry.getValue().forEach(
                            (Integer courseId) -> {
                                EinA elemInSetConst = new EinA(courseId, periodVar);
                                store.impose(elemInSetConst);
                            });
                });
    }

}
