(ns visa-automation.core
  (:require [etaoin.api :as api]
            [clj-http.client :as http]
            [clojure.tools.logging :as log])
  (:gen-class))

(def interval-ms (* 20 60 1000))
(def tg-token "****")
(def tg-chat-id "-****")
(def username "****")
(def password "****")
(def chrome-options {:user-agent (str "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
                                      " AppleWebKit/537.36 (KHTML, like Gecko)"
                                      " Chrome/102.0.0.0 Safari/537.36")
                     :size [1920 1280]})

(defn send-telegram-message [message]
  (let [url (str "https://api.telegram.org/bot" tg-token "/sendMessage")
        params {:accept :json
                :throw-exceptions false
                :query-params {"chat_id" tg-chat-id
                               "text" message}}]
    (http/get url params)))

(defn run-check []
  (try
    (log/info "Run check")
    (let [driver (api/chrome-headless chrome-options)]
      (api/go driver "https://visa.vfsglobal.com/blr/ru/nor/login")
      (api/wait-visible driver {:tag :h1 :fn/text "Войти"})
      (api/fill driver {:tag :input :id :mat-input-0} username)
      (api/fill driver {:tag :input :id :mat-input-1} password)
      (api/click driver {:tag :span :class :mat-button-wrapper :fn/text " Войти "})
      (api/wait 10)
      (->> (api/query-all driver {:tag :span :class :mat-button-wrapper :fn/text " Записаться на прием "})
           (filter #(api/displayed-el? driver %))
           (first)
           (api/click-el driver))
      (api/wait 10)
      (api/scroll-bottom driver)
      (api/click driver {:tag :div :id :mat-select-value-3})
      (api/click driver {:tag :span :class :mat-option-text :fn/text " Visa C "})
      (api/wait 10)
      (if (api/has-text? driver "В настоящее время нет свободных мест для записи")
        (send-telegram-message "Нет мест")
        (send-telegram-message "Есть места для записи"))
      (api/quit driver))
    (catch Exception e
      (send-telegram-message "Check failed")
      (log/error e "Check failed"))))

(defn -main [& _]
  (while true
    (do
      (run-check)
      (Thread/sleep interval-ms))))
