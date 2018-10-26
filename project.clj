(defproject democracyworks/resource-config "1.0.0-SNAPSHOT"
  :description "Simple (too simple?) configuration handling"
  :url "http://github.com/democracyworks/resource-config"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [org.clojure/core.memoize "0.5.6"]]
  :profiles {:test {:resource-paths ["test-resources"]}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
