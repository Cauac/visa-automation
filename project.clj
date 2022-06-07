(defproject visa-automation "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [etaoin "0.4.6"]
                 [clj-http "3.12.3"]]

  :plugins [[lein-ancient "0.7.0"]]

  :repl-options {:init-ns visa-automation.core}

  :main visa-automation.core
  :profiles {:uberjar {:aot :all
                       :uberjar-name "visa-automation.jar"}}
  )
