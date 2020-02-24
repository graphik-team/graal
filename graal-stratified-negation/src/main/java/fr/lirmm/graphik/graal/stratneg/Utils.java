package fr.lirmm.graphik.graal.stratneg;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Var;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.SccChase;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

@CheckReturnValue
public class Utils {

  private static AtomicInteger i_ = new AtomicInteger(-1);

  public static RuleWithNegation parseRule(String string) throws ParseException {

    Preconditions.checkNotNull(string, "string is null");

    LinkedListAtomSet posBody = new LinkedListAtomSet();
    LinkedListAtomSet negBody = new LinkedListAtomSet();

    Rule rule = DlgpParser.parseRule(string);

    for (Predicate predicate : rule.getBody().getPredicates()) {
      try (CloseableIteratorWithoutException<Atom> iterator =
          rule.getBody().atomsByPredicate(predicate)) {

        while (iterator.hasNext()) {

          Atom atom = iterator.next();
          Predicate pred = atom.getPredicate();

          if (!pred.toString().startsWith("not_")) {
            posBody.add(atom);
          } else {
            atom.setPredicate(new Predicate(pred.getIdentifier().toString().replaceAll("not_", ""),
                pred.getArity()));
            negBody.add(atom);
          }
        }
      }
    }
    return new RuleWithNegation(Integer.toString(i_.incrementAndGet(), 10), posBody, negBody,
        rule.getHead());
  }

  private static void fillKb(KBBuilder kbb, String fileRules, String fileFacts) {

    Preconditions.checkNotNull(kbb, "kbb is null");

    // Parsing Rules
    if (fileRules != null) {

      System.out.println("Rules : parsing of '" + fileRules + "'");

      try (BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(fileRules), UTF_8))) {

        @Var
        String row;

        while ((row = br.readLine()) != null) {
          if (row.length() > 0 && row.charAt(0) != '%') {
            kbb.add(parseRule(row));
          }
        }
      } catch (Exception e) {
        Throwables.getRootCause(e).printStackTrace();
      }
    }

    // Parsing Facts
    if (fileFacts != null) {

      System.out.println("Facts : parsing of '" + fileFacts + "'");

      try (BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(fileFacts), UTF_8))) {

        @Var
        String row;

        while ((row = br.readLine()) != null) {
          if (row.length() > 0 && row.charAt(0) != '%') {
            kbb.add(DlgpParser.parseAtom(row));
          }
        }
      } catch (Exception e) {
        Throwables.getRootCause(e).printStackTrace();
      }
    }
  }

  private static String displayFacts(AtomSet facts) {

    Preconditions.checkNotNull(facts, "facts is null");

    StringBuilder sb = new StringBuilder("== Saturation ==\n");

    try (CloseableIterator<Atom> iterator = facts.iterator()) {
      while (iterator.hasNext()) {
        sb.append(iterator.next().toString());
        sb.append(".\n");
      }
    } catch (IteratorException e) {
      Throwables.getRootCause(e).printStackTrace();
    }
    return sb.toString();
  }

  public static String getSaturationFromFile(String src, LabeledGraphOfRuleDependencies grd) {

    Preconditions.checkNotNull(src, "src is null");
    Preconditions.checkNotNull(grd, "grd is null");

    KBBuilder kbb = new KBBuilder();
    Utils.fillKb(kbb, null, src);
    KnowledgeBase kb = kbb.build();
    SccChase<AtomSet> chase = new SccChase<>(grd, kb.getFacts(), new RuleApplierWithNegation<>());

    try {
      chase.execute();
    } catch (ChaseException e) {
      Throwables.getRootCause(e).printStackTrace();
    }
    return Utils.displayFacts(kb.getFacts());
  }

  public static String getRulesText(Iterable<Rule> rules) {

    Preconditions.checkNotNull(rules, "rules is null");

    StringBuilder sb = new StringBuilder("====== RULE SET ======\n");

    for (Rule rule : rules) {
      sb.append(rule.toString());
      sb.append('\n');
    }
    return sb.toString();
  }

  public static String getGrdText(LabeledGraphOfRuleDependencies grd) {

    Preconditions.checkNotNull(grd, "grd is null");

    StringBuilder sb = new StringBuilder("======== GRD =========\n");

    for (Rule rule1 : grd.getRules()) {
      for (Rule rule2 : grd.getTriggeredRules(rule1)) {
        sb.append("[");
        sb.append(rule1.getLabel());
        sb.append("] ={+}=> [");
        sb.append(rule2.getLabel());
        sb.append("]\n");
      }
      for (Rule r2 : grd.getInhibitedRules(rule1)) {
        sb.append("[");
        sb.append(rule1.getLabel());
        sb.append("] ={-}=> [");
        sb.append(r2.getLabel());
        sb.append("]\n");
      }
    }
    return sb.toString();
  }

  public static String getSccText(StronglyConnectedComponentsGraph<Rule> scc) {

    Preconditions.checkNotNull(scc, "scc is null");

    StringBuilder sb = new StringBuilder("======== SCC =========\n");

    for (int i = 0; i < scc.getNbrComponents(); i++) {

      @Var
      boolean first = true;

      sb.append("C");
      sb.append(i);
      sb.append(" = {");

      for (Rule rule : scc.getComponent(i)) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(rule.getLabel());
      }
      sb.append("}\n");
    }
    return sb.toString();
  }
}
