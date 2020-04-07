# minicitrus.alpha

Simplified state management library for ClojureScript inspired by Citrus

<This is alpha... use at your own risk.>

## DEPS

If you're copying this the second time leave a star

tools.deps
``` edn
mdrobulis/minicitrus {git/url "https://github.com/mdrobulis/minicitrus" :sha <look it up> }
```

## Motivation and GOALS

Inspired by [citrus](https://github.com/clj-commons/citrus) and [re-frame](https://github.com/Day8/re-frame) event dispach function.
Demotivated by too many concepts

So I made this thing.

Goals
- remove event handler code from inside your componet
- remove the inline handler functions  "#(..." or "(fn [e] ..." 
- access to a global atom for state changes
- Easy to learn.
- Close to bare metal
- Hide core.async

## Usage


You have 2 entry points

``` clojure
(publish ::your-event-key data)  ;; for use inside components for event handlers.
`
``` clojure
(defmethod handler ::your-event-key [_ data]
   #_do_stuff
 )
```
Default handler logs all the details of the event into console

publish and handler are variadic

First argument of handler is a map that includes
- :argument of the event
- :key  event routing key
- :state global state atom defined in minicitrus ns. feel free to use it or not.


inside your code
``` clojure
(your.ns
   (:require [minicitrus.alpha :as mc])
)

(defmethod mc/handler ::add-product  [global product]
    (let [state (global :state)]
       (swap! state conj product)       
     )
)

(rum/defc product-list [product-list]
  [:div
    (let [product product-list]
     [:div
       [:label (:name item)]
       [:button
          {:on-click (mc/publish ::add-product product ) }
          "Add"]
       [:button
          {:on-click (mc/publsh  ::remover-product product )}
          "Remove"]
       ])]
       )
```


# Under the hood

- single core.async chanel for event aggregation
- Event loop that routes the events to the handler multimethod

# Contributions and feedback

pull requests are welcome for

- [ ] cljc implementation for use in Java UI
- [ ] better ways to inject different kinds of state.  // Reagent atoms, datascript
- [ ] Tests

## License

Copyright © 2020 Martynas Drobulis

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
