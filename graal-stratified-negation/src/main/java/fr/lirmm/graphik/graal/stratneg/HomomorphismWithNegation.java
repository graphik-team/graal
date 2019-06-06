package fr.lirmm.graphik.graal.stratneg;

import java.util.ArrayList;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

@CheckReturnValue
public class HomomorphismWithNegation extends AbstractProfilable
    implements HomomorphismWithCompilation<Object, AtomSet> {

  private static HomomorphismWithNegation instance_;

  public HomomorphismWithNegation() {

  }

  public static synchronized HomomorphismWithNegation instance() {
    if (instance_ == null) {
      instance_ = new HomomorphismWithNegation();
    }
    return instance_;
  }

  @Override
  public boolean exist(Object q, AtomSet a, RulesCompilation compilation) {
    return exist(q, a);
  }

  @Override
  public boolean exist(Object q, AtomSet a, RulesCompilation compilation, Substitution s)
      throws HomomorphismException {
    return false;
  }

  @Override
  public boolean exist(Object q, AtomSet a) {

    Preconditions.checkNotNull(q, "q is null");
    Preconditions.checkArgument(q instanceof ConjunctiveQueryWithNegation,
        "q is not an instance of ConjunctiveQueryWithNegation");
    Preconditions.checkNotNull(a, "a is null");

    ConjunctiveQueryWithNegation cq = (ConjunctiveQueryWithNegation) q;

    try (CloseableIterator<Substitution> l = SmartHomomorphism.instance()
        .execute(new DefaultConjunctiveQuery(cq.positiveAtomSet()), a)) {
      while (l.hasNext()) {

        Substitution s = l.next();

        if (verifSub(s, cq.negativeAtomSet(), a)) {
          return true;
        }
      }
    } catch (HomomorphismException e) {
      e.printStackTrace();
    } catch (IteratorException e) {
      e.printStackTrace();
    } catch (AtomSetException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean exist(Object q, AtomSet a, Substitution s) {
    return false;
  }

  @Override
  public CloseableIterator<Substitution> execute(Object q, AtomSet a) {

    Preconditions.checkNotNull(q, "q is null");
    Preconditions.checkArgument(q instanceof ConjunctiveQueryWithNegation,
        "q is not an instance of ConjunctiveQueryWithNegation");
    Preconditions.checkNotNull(a, "a is null");

    ConjunctiveQueryWithNegation cq = (ConjunctiveQueryWithNegation) q;
    ArrayList<Substitution> list = new ArrayList<>();

    try (CloseableIterator<Substitution> l = SmartHomomorphism.instance()
        .execute(new DefaultConjunctiveQuery(cq.positiveAtomSet()), a)) {
      while (l.hasNext()) {

        Substitution s = l.next();

        if (verifSub(s, cq.negativeAtomSet(), a)) {
          list.add(s);
        }
      }
    } catch (HomomorphismException e) {
      e.printStackTrace();
    } catch (IteratorException e) {
      e.printStackTrace();
    } catch (AtomSetException e) {
      e.printStackTrace();
    }
    return new CloseableIteratorAdapter<>(list.iterator());
  }

  @Override
  public CloseableIterator<Substitution> execute(Object q, AtomSet a, Substitution s) {
    return null;
  }

  @Override
  public CloseableIterator<Substitution> execute(Object q, AtomSet a,
      RulesCompilation compilation) {
    return execute(q, a);
  }

  @Override
  public CloseableIterator<Substitution> execute(Object q, AtomSet a, RulesCompilation compilation,
      Substitution s) {
    return null;
  }

  private boolean verifSub(Substitution sub, AtomSet negPart, AtomSet factBase)
      throws AtomSetException {

    Preconditions.checkNotNull(sub, "sub is null");
    Preconditions.checkNotNull(negPart, "negPart is null");
    Preconditions.checkNotNull(factBase, "factBase is null");

    LinkedListAtomSet res = new LinkedListAtomSet();
    sub.apply(negPart, res);

    for (Predicate predicate : res.getPredicates()) {
      try (CloseableIteratorWithoutException<Atom> it = res.atomsByPredicate(predicate)) {
        while (it.hasNext()) {

          Atom atom = it.next();

          if (factBase.contains(atom)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
