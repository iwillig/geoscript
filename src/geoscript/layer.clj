(ns geoscript.layer)

(defprotocol ILayer
  (add          [this])
  (get-features [this])
  (get-bounds   [this]))

