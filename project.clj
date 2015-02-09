(defproject turbovote.resource-config "0.1.4"
  :description "Simple (too simple?) configuration handling"
  :url "http://github.com/turbovote/resource-config"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:test {:resource-paths ["test-resources"]}})
