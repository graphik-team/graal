/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.util;

import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Apps {
	
	private Apps(){}
	
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
		} catch (Exception ex) {
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
		String pathToClass = Apps.class.getResource(classSimpleName)
				.toString();
		// pathToClass =
		// file:/C:/workspace/VersionUtil/bin/com/abdennebi/version/VersionUtil.class
		// pathToClass =
		// jar:file:/C:/version.jar!/com/abdennebi/version/VersionUtil.class

		// 3 - Récupérer le chemin de la classe à partir de la racine du
		// classpath
		String classFullName = Apps.class.getName().replace('.', '/')
				+ ".class";
		// classFullName = com/abdennebi/version/VersionUtil.class

		// 4 - Récupérer le chemin complet vers MANIFEST.MF
		String pathToManifest = pathToClass.substring(0, pathToClass.length()
				- (classFullName.length()))
				+ "META-INF/MANIFEST.MF";
		// pathToManifest =
		// file:/C:/workspace/VersionUtil/bin/META-INF/MANIFEST.MF
		// pathToManifest = jar:file:/C:/version.jar!/META-INF/MANIFEST.MF

		return pathToManifest;
	}


}
