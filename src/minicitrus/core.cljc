(ns minicitrus.core
  (:require [cljs.core.async :as async
             :refer [<! >! chan close! sliding-buffer dropping-buffer
                     put! timeout]])
  (:require-macros
   [cljs.core.async.macros :as m :refer [go alt! go-loop]])
  )



(defonce app-state (atom {}))

;; Chanel size is directly proportional the amout of calls publish calls you can make inside a handler.
(def^ event-hub (chan 10) )




(defn publish
  "Warning!!! For event handlers use publish-fn
  pushes data to the hub,
  can be used inside handlers to trigger other handlers
  "
  [key & args]
  (go
    (>! event-hub
        {:args args
         :state app-state
         :key key  } ))
  nil
  )
  

(defn publish-fn
  "usage in hiccup:
  [:button {:on-click (publish ::add-to-cart procuct )} \"Add to cart\"  ]
  
  returns a handler fn that plugs into global event routing"
  [key & args]
  (fn [& e] 
    (go (>! event-hub 
            {:key key 
             :event e 
             :state app-state 
             :args args})))
  )

(defmulti handler 
  "usage:
  (handler ::your-event-key 
  [{:keys [key event state args]}] 
    (swap! state assoc :args args))

  key: event dispatch key used by the multimethod
  event: args provided by the event
  state: global state atom
  args: arguments given to the publish fn" (fn [{:keys [key]} & args] key))

(defn^ event-loop []
  (go-loop [_ nil] 
    (when-let [event (<! event-hub)] 
      (recur (apply handler event (:args event)))))
  )

(event-loop)  ;; Initialization of the event loop

(defmethod handler :default [{:keys [key] :as event} & args ] 
  (.error js/console  (str "Unhadled call to (publish " key " args)\r\n"
                           "event " event "\r\nargs  " args)) )   


(comment 
  ((publish ::test ["wasa"]) :event)

  ((publish ::add-items [1 2 3 5 ]  :e :v))

  @app-state
  (reset! app-state {}  )
  (swap! app-state assoc :items [] )
  (swap! app-state assoc :items [] )
  ((publish ::add-items [5 4 3 2 1] ) :e)
  )
