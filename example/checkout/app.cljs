(ns checkout.app
  (:require [re-learn.core :as re-learn]
            [re-learn.utils :as rlu]
            [re-learn.views :as re-learn-views]
            [reagent.core :as reagent]))

(def purchase-button
  (rlu/with-lesson
    {:id :purchase-button-lesson
     :description "When you're ready, click here to purchase"
     :position :top}

    (fn [] [:button.mdl-button.mdl-button--raised
            {:style {:margin-top 10}}
            "Purchase"])))

(defn actions []
  [:div [purchase-button]])

(def totals
  (rlu/with-lesson
    {:id :totals-lesson
     :description "The total amount of your basket appears here"
     :position :right}

    (fn [items]
      [:tr
       [:td {:col-span 2} "Total"]
       [:td (reduce + (map :sub-total-price @items))]])))

(defn- basket-item [{:keys [name quantity unit-price sub-total-price]}]
  [:tr.basket-item
   [:td name]
   [:td quantity " @ " unit-price]
   [:td sub-total-price]])

(def basket
  (rlu/with-lesson
    {:id :basket-lesson
     :description "This is your basket where all the items you want to purchase appear. Click on an item to continue."
     :position :left
     :attach [:#basket :.basket-item]
     :continue [:#basket :.basket-item]}

    (fn [items]
      [:table#basket.mdl-data-table
       [:thead
        [:tr
         [:th "Name"]
         [:th "Quantity"]
         [:th "Sub-total"]]]
       [:tbody
        (doall (for [{:keys [id] :as item} @items]
                 ^{:key id}
                 [basket-item item]))
        [totals items]]])))

(def help-link
  (rlu/with-lesson
    {:id :help
     :description "Click here to run the tutorial again"
     :position :bottom}

    (fn []
      [:a {:href "#"
           :on-click re-learn/reset-education!}
       "Help"])))

(def checkout
  (rlu/with-tutorial
    {:id :checkout-tutorial
     :name "The checkout"
     :description "Review your basket, check the price and confirm your purchase"
     :lessons [{:id :welcome
                :description [:div
                              [:h2 "Welcome"]
                              "Welcome to the re-learn example"]}
               basket
               totals
               purchase-button
               help-link]}

    (fn [app-state]
      [:div {:style {:position "absolute"
                     :left "50%"
                     :top "20%"
                     :transform "translate(-50%, -50%)"}}
       [basket app-state]
       [actions]
       [help-link]])))

(defn- on-figwheel-reload []
  (reagent/force-update-all))

(defn- init []
  (let [app-root (js/document.getElementById "app")
        tutorial-root (js/document.getElementById "tutorial")
        app-state (reagent/atom [{:id "apples"
                                  :name "Apples"
                                  :quantity 2
                                  :unit-price 0.3
                                  :sub-total-price 0.6}

                                 {:id "oranges"
                                  :name "Oranges"
                                  :quantity 5
                                  :unit-price 0.25
                                  :sub-total-price 1.25}])]
    (re-learn/init)

    (reagent/render [checkout app-state] app-root)
    (reagent/render [re-learn-views/tutorial] tutorial-root)))

(.addEventListener js/document "DOMContentLoaded" init)
