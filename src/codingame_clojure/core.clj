(ns codingame-clojure.core)

(defn swap [v i1 i2]
  (assoc v i2 (v i1) i1 (v i2)))

(defn adjust-swappers [p]
  #{(- (first p) 1), (+ (second p) 1)}
  )

(defn process [^String s]
  (group-by #(.charAt s %) (range (count s))))

(defn get-swappers [line]
  (adjust-swappers (partition 2  (get (process line) \-))))

(defn get-swappers-2 [line]
  (partition 2  (get (process line) \-)))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
