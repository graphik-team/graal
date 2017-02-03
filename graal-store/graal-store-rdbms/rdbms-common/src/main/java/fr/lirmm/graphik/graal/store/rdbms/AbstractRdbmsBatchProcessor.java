/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.store.BatchProcessor;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractRdbmsBatchProcessor implements BatchProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRdbmsBatchProcessor.class);

	private final int MAX_BATCH_SIZE;
	private int unflushedAtoms = 0;
	private Statement statement = null;
	private Connection connection = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public AbstractRdbmsBatchProcessor(Connection con) throws AtomSetException {
		this(con, 1024);
	}

	public AbstractRdbmsBatchProcessor(Connection con, int batchSize) throws AtomSetException {
		this.connection = con;
		try {
			this.statement = this.connection.createStatement();
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		this.MAX_BATCH_SIZE = batchSize;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected abstract void add(Statement statement, Atom a) throws AtomSetException;

	@Override
	public void addAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		try {
			while (it.hasNext())
				this.add(it.next());
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public void add(Atom a) throws AtomSetException {
		this.add(this.statement, a);
		++this.unflushedAtoms;
		if (this.unflushedAtoms >= MAX_BATCH_SIZE) {
			this.flush();

		}
	}

	@Override
	public void flush() throws AtomSetException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch flush, size=" + MAX_BATCH_SIZE);
		}
		if (this.statement != null) {
			try {
				this.statement.executeBatch();
				this.unflushedAtoms = 0;
			} catch (SQLException e) {
				throw new AtomSetException(e);
			}
		}
	}

	@Override
	public void commit() throws AtomSetException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch commit");
		}
		if (this.statement != null) {
			try {
				this.flush();
				this.connection.commit();
			} catch (Exception e) {
				throw new AtomSetException(e);
			}
		}
	}

	@Override
	public void close() {
		if (this.statement != null) {
			try {
				this.statement.close();
			} catch (SQLException e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
