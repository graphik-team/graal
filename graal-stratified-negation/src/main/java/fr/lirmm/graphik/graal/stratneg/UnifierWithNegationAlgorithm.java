package fr.lirmm.graphik.graal.stratneg;

import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Var;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.unifier.UnifierChecker;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.unifier.DefaultUnifierAlgorithm;
import fr.lirmm.graphik.graal.core.unifier.checker.ProductivityChecker;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

@CheckReturnValue
public class UnifierWithNegationAlgorithm {

  private static UnifierWithNegationAlgorithm instance_;
  private final UnifierChecker[] tab_ = {};

  public static synchronized UnifierWithNegationAlgorithm instance() {
    if (instance_ == null) {
      instance_ = new UnifierWithNegationAlgorithm();
    }
    return instance_;
  }

  private static boolean hasIntersection(InMemoryAtomSet a1, InMemoryAtomSet a2) {
    return a1.removeAll(a2);
  }

  public boolean existPositiveDependency(RuleWithNegation src, RuleWithNegation dest) {

    RuleWithNegation r1 =
        createImageOf(src, DefaultUnifierAlgorithm.getSourceVariablesSubstitution());
    RuleWithNegation r2 =
        createImageOf(dest, DefaultUnifierAlgorithm.getTargetVariablesSubstitution());

    // Compute Piece unifiers
    try (CloseableIteratorWithoutException<Substitution> sigmas = DefaultUnifierAlgorithm.instance()
        .computePieceUnifier(r1, r2, ProductivityChecker.instance())) {
      while (sigmas.hasNext()) {
        if (isValidPositiveUnifier(r1, r2, sigmas.next())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean existNegativeDependency(RuleWithNegation src, RuleWithNegation dest) {

    LinkedListAtomSet r1Head = new LinkedListAtomSet();

    try (CloseableIterator<Atom> itAtom = src.getHead().iterator()) {

      @Var
      boolean add;

      while (itAtom.hasNext()) {
        Atom a = itAtom.next();
        add = true;
        for (Term t : a.getTerms()) {
          if (t.isVariable()) {
            if (src.getExistentials().contains(t)) {
              add = false;
              break;
            }
          }
        }
        if (add) {
          r1Head.add(a);
        }
      }
    } catch (IteratorException e) {
      Throwables.getRootCause(e).printStackTrace();
    }

    RuleWithNegation srcBis =
        new RuleWithNegation(src.getLabel(), src.getBody(), src.negativeBody(), r1Head);
    RuleWithNegation r1 =
        createImageOf(srcBis, DefaultUnifierAlgorithm.getSourceVariablesSubstitution());
    RuleWithNegation r2 =
        createImageOf(dest, DefaultUnifierAlgorithm.getTargetVariablesSubstitution());

    try (CloseableIteratorWithoutException<Substitution> sigmas =
        DefaultUnifierAlgorithm.instance().computePieceUnifier(r1, r2.negativeBody(), tab_)) {
      while (sigmas.hasNext()) {
        if (isValidNegativeUnifier(r1, r2, sigmas.next())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValidPositiveUnifier(RuleWithNegation r1, RuleWithNegation r2, Substitution s) {

    /* Application substitution */
    InMemoryAtomSet bpi = s.createImageOf(r1.getBody());
    InMemoryAtomSet bni = s.createImageOf(r1.negativeBody());
    InMemoryAtomSet bpj = s.createImageOf(r2.getBody());
    InMemoryAtomSet bnj = s.createImageOf(r2.negativeBody());
    InMemoryAtomSet hi = s.createImageOf(r1.getHead());
    InMemoryAtomSet hj = s.createImageOf(r2.getHead());

    boolean i = !hasIntersection(bpi, bni);
    boolean ii = !hasIntersection(bpi, bnj);
    boolean iii = !hasIntersection(bpj, bnj);

    InMemoryAtomSet bpjBis = s.createImageOf(bpj);
    bpjBis.removeAll(hi);
    boolean iv = !hasIntersection(bni, bpjBis);

    InMemoryAtomSet union = new LinkedListAtomSet();
    union.addAll(hi); // Atomic heads
    union.addAll(bpi);
    union.addAll(bpj);

    @Var
    boolean v = true;

    try (CloseableIterator<Atom> itAtom = hj.iterator()) {
      while (itAtom.hasNext()) {
        if (union.contains(itAtom.next())) {
          v = false;
          break;
        }
      }
    } catch (IteratorException e) {
      Throwables.getRootCause(e).printStackTrace();
    }

    boolean vi = !hasIntersection(bnj, hi);
    @Var
    boolean vii = true;

    try (CloseableIterator<Atom> itAtom = bpj.iterator()) {
      while (itAtom.hasNext()) {
        if (bpi.contains(itAtom.next())) {
          vii = false;
          break;
        }
      }
    } catch (IteratorException e) {
      Throwables.getRootCause(e).printStackTrace();
    }

    bpi.clear();
    bni.clear();
    bpj.clear();
    bpjBis.clear();
    bnj.clear();
    hi.clear();
    hj.clear();
    union.clear();

    return (i && ii && iii && iv && v && vi && vii);
  }

  private boolean isValidNegativeUnifier(RuleWithNegation r1, RuleWithNegation r2, Substitution s) {

    /* Application substitution */
    InMemoryAtomSet bpi = s.createImageOf(r1.getBody());
    InMemoryAtomSet bni = s.createImageOf(r1.negativeBody());
    InMemoryAtomSet bpj = s.createImageOf(r2.getBody());
    InMemoryAtomSet bnj = s.createImageOf(r2.negativeBody());

    InMemoryAtomSet uPos = new LinkedListAtomSet();
    uPos.addAll(bpi);
    uPos.addAll(bpj);

    InMemoryAtomSet uNeg = new LinkedListAtomSet();
    uNeg.addAll(bni);
    uNeg.addAll(bnj);

    /* (i) */
    boolean inter = hasIntersection(uPos, uNeg); // inter = (B+1 , B+2) inter (B-1 , B-2)

    bpi.clear();
    bni.clear();
    bpj.clear();
    bnj.clear();
    uPos.clear();
    uNeg.clear();

    return (!inter);
  }

  private RuleWithNegation createImageOf(RuleWithNegation rule, Substitution s) {
    return new RuleWithNegation(rule.getLabel(), s.createImageOf(rule.getBody()),
        s.createImageOf(rule.negativeBody()), s.createImageOf(rule.getHead()));
  }
}
