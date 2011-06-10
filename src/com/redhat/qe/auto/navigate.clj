(ns com.redhat.qe.auto.navigate
  (:require [clojure.zip :as zip]
            [clojure.set])
  (:import [java.util NoSuchElementException]))

(defmacro nav-tree "takes a nested vector structure and returns nested maps" [[page-id args form & links]]
  (merge `{:page ~page-id
           :fn (fn ~args ~form)
           :req-args ~(vec (map keyword args))}
         (if links
           {:links (vec (for [link links]
                          `(nav-tree ~link)))}
           {})))

(defn page-zip [tree] (zip/zipper (constantly true)
                          #(:links %)
                          #(conj %1 {:links %2})
                          tree))

(defn find-node [z pred]
  (and z (->> (iterate zip/next z)
              (take-while #(not (zip/end? %1)))
              (some  #(when (pred (zip/node %)) %)))))

(defn page-path [page z]
  (let [page-match? #(= (:page %) page)
        first-match (find-node z page-match?)]
    (if (nil? first-match)
      (throw (NoSuchElementException. (str "Page " page " was not found in navigation tree."))))
    (conj (zip/path first-match) (zip/node first-match))))

(defn navigate
  ([page z args]
     (let [path (page-path page z)
           all-req-args (set (mapcat :req-args path))
           missing-args (clojure.set/difference all-req-args (set (keys args)))]
       (if-not (zero? (count missing-args))
         (throw (IllegalArgumentException. (str "Missing required keys to navigate to " page " - " missing-args)))
         (doseq [step path]
           (apply (:fn step)
                  (for [req-arg (:req-args step)]
                    (req-arg args)))))))
  ([page z] (navigate page z {})))

(defn nav-fn "Closes over a page tree structure and returns a navigation function"
  [tree]
  (let [z (page-zip tree)]
   (fn
     ([page args]
        (navigate page z args))
     ([page] (navigate page z {})))))