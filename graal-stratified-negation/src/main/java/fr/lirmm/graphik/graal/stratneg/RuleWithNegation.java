package fr.lirmm.graphik.graal.stratneg;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

@CheckReturnValue
public class RuleWithNegation extends DefaultRule {

  private final InMemoryAtomSet negativeBody_;
  private final int indice_;

  public RuleWithNegation(String label, InMemoryAtomSet positiveBody, InMemoryAtomSet negativeBody,
      InMemoryAtomSet head) {

    super(label, positiveBody, head);

    Preconditions.checkNotNull(negativeBody, "negativeBody is null");

    negativeBody_ = negativeBody;
    indice_ = Integer.parseInt(getLabel(), 10);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    appendTo(sb);
    return sb.toString();
  }

  @Override
  public void appendTo(StringBuilder sb) {

    Preconditions.checkNotNull(sb, "sb is null");

    if (!getLabel().isEmpty()) {
      sb.append('[');
      sb.append(getLabel());
      sb.append("] ");
    }

    sb.append("[");

    // Positive body
    for (Predicate predicate : getBody().getPredicates()) {
      try (CloseableIteratorWithoutException<Atom> it = getBody().atomsByPredicate(predicate)) {
        while (it.hasNext()) {
          Atom atom = it.next();
          sb.append(atom.toString());
          sb.append(" , ");
        }
      }
    }

    sb.replace(sb.length() - 2, sb.length(), "");

    // Negative body
    for (Predicate predicate : negativeBody().getPredicates()) {
      try (
          CloseableIteratorWithoutException<Atom> it = negativeBody().atomsByPredicate(predicate)) {
        while (it.hasNext()) {
          Atom atom = it.next();
          sb.append(" , !");
          sb.append(atom.toString());
        }
      }
    }

    sb.append("] -> ");
    sb.append(getHead());
  }

  /**
   * Get the negative body (the hypothesis) of this rule.
   *
   * @return the body of this rule.
   */
  public InMemoryAtomSet negativeBody() {
    return negativeBody_;
  }

  public int indice() {
    return indice_;
  }
}
