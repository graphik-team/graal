package fr.lirmm.graphik.graal.chase_bench.io;

import java.util.List;

import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;



interface InputProcessor {
    void startProcessing();
    void processRule(Rule rule);
    void setFactPredicate(Predicate predicate);

	void processFact(List<Object> argumentLexicalForms, List<Constant.Type> argumentTypes);
    void endProcessing();
}
