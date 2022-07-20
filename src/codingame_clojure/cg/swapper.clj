(ns codingame-clojure.cg.swapper)

(defn swap [v i1 i2]
  (assoc v i2 (v i1) i1 (v i2)))

(defn swap [v ipair]
  (assoc v (nth ipair 1) (v (nth ipair 0)) (nth ipair 0) (v (nth ipair 1))))

(defn adjust-swappers [p]
  #{(- (first p) 1), (+ (second p) 1)}
  )

(defn ad-s [p]
  (list (+ (nth p 1) 1)
        (- (nth p 0) 1)))


(defn process [^String s]
  (group-by #(.charAt s %) (range (count s))))

(defn get-swappers-2 [line]
  (partition 2  (get (process line) \-)))

(defn get-swappers [line]
  (map ad-s (get-swappers-2 line)))

(defn ad-line [result swappers]
  (if (empty? swappers)
    result
    (ad-line (swap result (first swappers)) (rest swappers)))
  )
