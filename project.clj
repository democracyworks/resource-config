(defproject democracyworks/resource-config "1.1.0"
  :description "Simple (too simple?) configuration handling"
  :url "http://github.com/democracyworks/resource-config"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [aero "1.1.3"]
                 [org.clojure/core.memoize "0.5.6"]]
  :profiles {:test {:resource-paths ["test-resources"]}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  :env-vars {:EXAMPLE_VAR "123"}
  :plugins [[lein-with-env-vars "0.2.0"]])
