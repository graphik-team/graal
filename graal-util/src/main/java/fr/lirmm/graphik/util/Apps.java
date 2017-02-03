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
/**
 * 
 */
package fr.lirmm.graphik.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Apps {

	private Apps() {
	}

	public static void printVersion(String applicationName) {
		Manifest manifest;
		InputStream is;
		Attributes att;
		URL pathToManifest;

		String version;
		String vendor;
		String buildDate;

		// GET DATA
		try {
			pathToManifest = new URL(getPathToManifest());
			is = pathToManifest.openStream();
			manifest = new Manifest(is);
			att = manifest.getMainAttributes();

			version = att.getValue("Specification-Version");
			vendor = att.getValue("Specification-Vendor");
			buildDate = att.getValue("Built-On");

			is.close();
		} catch (IOException ex) {
			version = vendor = buildDate = "?";
		}

		// PRINT DATA
		System.out.print(applicationName);
		System.out.print(" version \"");
		System.out.print(version);
		System.out.println("\"");
		System.out.print("Built on ");
		System.out.println(buildDate);
		System.out.print("Produced by ");
		System.out.println(vendor);
	}

	private static String getPathToManifest() {

		// 1 - Lire le nom de la classe
		String classSimpleName = Apps.class.getSimpleName() + ".class";
		// classSimpleName = VersionUtil.class

		// 2 - Récupérer le chemin physique de la classe
		String pathToClass = Apps.class.getResource(classSimpleName).toString();
		// pathToClass =
		// file:/C:/workspace/VersionUtil/bin/com/abdennebi/version/VersionUtil.class
		// pathToClass =
		// jar:file:/C:/version.jar!/com/abdennebi/version/VersionUtil.class

		// 3 - Récupérer le chemin de la classe à partir de la racine du
		// classpath
		String classFullName = Apps.class.getName().replace('.', '/') + ".class";
		// classFullName = com/abdennebi/version/VersionUtil.class

		// 4 - Récupérer le chemin complet vers MANIFEST.MF
		String pathToManifest = pathToClass.substring(0, pathToClass.length() - (classFullName.length()))
								+ "META-INF/MANIFEST.MF";
		// pathToManifest =
		// file:/C:/workspace/VersionUtil/bin/META-INF/MANIFEST.MF
		// pathToManifest = jar:file:/C:/version.jar!/META-INF/MANIFEST.MF

		return pathToManifest;
	}

}
