package se.uu.it.cs.recsys.constraint.constraints;

import org.jacop.core.Store;
import org.jacop.set.constraints.CardA;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.constraint.api.Solver;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseCardinalityConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseCardinalityConstraint.class);

    // 3. Cardinality const on each period [1, 3]
    public static void impose(Store store, SetVar[] periodVars) {
        LOGGER.debug("Posting cardinality constraint for each study period.");
        
        for (SetVar var : periodVars) {
            CardA cardConstraint = new CardA(var, Solver.MIN_COURSE_AMOUNT_PERIOD, Solver.MAX_COURSE_AMOUNT_PERIOD);
            store.impose(cardConstraint);
        }
    }
    
}
