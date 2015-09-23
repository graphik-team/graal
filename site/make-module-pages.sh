#!/bin/sh
cd $1
module=$(basename $1)
title=$module
description=$(awk -F "[><]" '/description/{print $3}' pom.xml)
deplist=$(mvn dependency:list | grep ":compile" | sort | uniq | sed 's/\[INFO\]\s*\([^:]*\):\([^:]*\):jar:\([^:]*\):compile.*/\1:\2:\3/' |  perl -n -e 'my $t; if (/(.*?):(.*)/) {$t = $2;} $_ = $1; s/[.:]/\//g; print "$_/$t\n"' | perl -n -e '/(.*)\/(.*)\/(.*)/ && print "/$1/$2/$3/$3.jar\n"' | sed 's@:@/@' | sed 's@:@-@')

echo '
<!DOCTYPE html>
<html>
  [%- PROCESS config.tt -%]
  [%- title = "'$title'" 
      config.basedir="../"
  -%]  
  [%- INCLUDE header.tt -%]
  
  <body>

    <!-- HEADER -->
    <div id="header_wrap" class="outer">
        <header class="inner">
          <h1 id="project_title">[% title %]</h1>
          [%- INCLUDE nav.tt -%]
        </header>
    </div>

    <!-- MAIN CONTENT -->
    <div id="main_content_wrap" class="outer">
      <section id="main_content" class="inner">

      <p>'$description'</p>

      <h2>Download</h2>
      <a href="[% "../" _ config.graalVersion _ "/'$module'-" _ config.graalVersion _ ".jar" %]">'$module'-[% config.graalVersion %].jar</a>

      <h2>Maven</h2>
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;fr.lirmm.graphik&lt;/groupId&gt;
    &lt;artifactId&gt;'$module'&lt;/artifactId&gt;
    &lt;version&gt;[% config.graalVersion %]&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>
      <h2>Dependency list</h2>
        <ul>'
for dep in $deplist
do
    echo '          <li><a href="https://repo1.maven.org/maven2'$dep'">'$(echo $dep | sed 's@^.*/\([^/]*\).jar$@\1@')'</a></li>'
done

echo '        </ul>
      </section>
    </div>

  [%- INCLUDE footer.tt -%]

  </body>
</html>'
