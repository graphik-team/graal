package fr.lirmm.graphik.graal.stratneg;

import java.util.LinkedList;
import java.util.List;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Var;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

@CheckReturnValue
public class ConjunctiveQueryWithNegation extends DefaultConjunctiveQuery
    implements Query, CloseableIterableWithoutException<Atom> {

  private InMemoryAtomSet positiveAtomSet_;
  private InMemoryAtomSet negativeAtomSet_;
  private List<Term> responseVariables_;
  private String label_;

  public ConjunctiveQueryWithNegation() {
    this("", DefaultAtomSetFactory.instance().create(), DefaultAtomSetFactory.instance().create(),
        new LinkedList<>());
  }

  public ConjunctiveQueryWithNegation(InMemoryAtomSet positiveAtomSet,
      InMemoryAtomSet negativeAtomSet) {
    this("", positiveAtomSet, negativeAtomSet, new LinkedList<>(positiveAtomSet.getVariables()));
  }

  public ConjunctiveQueryWithNegation(InMemoryAtomSet positiveAtomSet,
      InMemoryAtomSet negagtiveAtomSet, List<Term> ans) {
    this("", positiveAtomSet, negagtiveAtomSet, ans);
  }

  public ConjunctiveQueryWithNegation(String label, InMemoryAtomSet positiveAtomSet,
      InMemoryAtomSet negativeAtomSet, List<Term> ans) {

    Preconditions.checkNotNull(label, "label is null");
    Preconditions.checkNotNull(negativeAtomSet, "negativeAtomSet is null");
    Preconditions.checkNotNull(positiveAtomSet, "positiveAtomSet is null");
    Preconditions.checkNotNull(ans, "ans is null");

    positiveAtomSet_ = positiveAtomSet;
    negativeAtomSet_ = negativeAtomSet;
    responseVariables_ = ans;
    label_ = label;
  }

  public ConjunctiveQueryWithNegation(ConjunctiveQueryWithNegation query) {

    Preconditions.checkNotNull(query, "query is null");

    positiveAtomSet_ = DefaultAtomSetFactory.instance().create(query.positiveAtomSet());
    negativeAtomSet_ = DefaultAtomSetFactory.instance().create(query.negativeAtomSet());
    responseVariables_ = new LinkedList<>(query.getAnswerVariables());
    label_ = query.getLabel();
  }

  /**
   * The label (the name) for this query.
   *
   * @return the label of this query.
   */
  @Override
  public String getLabel() {
    return label_;
  }

  @Override
  public void setLabel(String label) {
    label_ = Preconditions.checkNotNull(label, "label is null");
  }

  /**
   * Get the atom conjunction representing the query.
   * 
   * @return an atom set representing the atom conjunction of the query.
   */
  public InMemoryAtomSet positiveAtomSet() {
    return positiveAtomSet_;
  }

  @Deprecated
  public void positiveAtomSet(InMemoryAtomSet positiveAtomSet) {
    positiveAtomSet_ = Preconditions.checkNotNull(positiveAtomSet, "positiveAtomSet is null");
  }

  /**
   * Get the negative atom conjunction representing the query.
   *
   * @return an atom set representing the atom conjunction of the query.
   */
  public InMemoryAtomSet negativeAtomSet() {
    return negativeAtomSet_;
  }

  @Deprecated
  public void negativeAtomSet(InMemoryAtomSet negativeAtomSet) {
    negativeAtomSet_ = Preconditions.checkNotNull(negativeAtomSet, "negativeAtomSet is null");
  }

  /**
   * Get the answer variables
   *
   * @return an Collection of Term representing the answer variables.
   */
  @Override
  public List<Term> getAnswerVariables() {
    return responseVariables_;
  }

  @Override
  public void setAnswerVariables(List<Term> v) {
    responseVariables_ = v;
  }

  @Override
  public boolean isBoolean() {
    return responseVariables_.isEmpty();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    this.appendTo(sb);
    return sb.toString();
  }

  @Override
  public void appendTo(StringBuilder sb) {

    Preconditions.checkNotNull(sb, "sb is null");

    sb.append("ANS(");

    @Var
    boolean first = true;

    for (Term term : this.responseVariables_) {
      if (!first) {
        sb.append(',');
      }
      first = false;
      sb.append(term);
    }

    sb.append(") : ");
    sb.append(this.positiveAtomSet_);
    sb.append(", !" + this.negativeAtomSet_);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ConjunctiveQueryWithNegation)) {
      return false;
    }
    ConjunctiveQueryWithNegation other = (ConjunctiveQueryWithNegation) obj;
    return Objects.equal(getAnswerVariables(), other.getAnswerVariables())
        && Objects.equal(positiveAtomSet(), other.positiveAtomSet())
        && Objects.equal(negativeAtomSet(), other.negativeAtomSet());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getAnswerVariables(), positiveAtomSet(), negativeAtomSet());
  }

  @Override
  public CloseableIteratorWithoutException<Atom> iterator() {
    return null;
  }

  /**
   * Return an iterator over the positive atoms conjunction of the query.
   */
  @Deprecated
  public CloseableIteratorWithoutException<Atom> positiveIterator() {
    return positiveAtomSet().iterator();
  }

  /**
   * Return an iterator over the negative atoms conjunction of the query.
   */
  @Deprecated
  public CloseableIteratorWithoutException<Atom> negativeIterator() {
    return negativeAtomSet().iterator();
  }
}
