(defproject turbovote.resource-config "0.2.1-SNAPSHOT"
  :description "Simple (too simple?) configuration handling"
  :url "http://github.com/turbovote/resource-config"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.3.0"]
                 [org.clojure/core.cache "0.6.5"]]
  :profiles {:dev {:resource-paths ["test-resources"]
                   :dependencies [[http-kit "2.2.0"]
                                  [compojure "1.5.1"]
                                  [javax.servlet/servlet-api "2.5"]]}
             :test {:resource-paths ["test-resources"]
                    :dependencies [[http-kit "2.2.0"]
                                   [compojure "1.5.1"]
                                   [javax.servlet/servlet-api "2.5"]]}})
