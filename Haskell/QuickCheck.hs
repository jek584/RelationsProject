import Test.QuickCheck
import Relation
import Data.List

isSame :: (Int, Int) -> Bool
isSame (x, y) = (x == y)

take5 :: [a] -> [a]
take5 arr = take 5 arr

prop_reflexive :: [(Int, Int)] -> Property
prop_reflexive arr = isreflexive arr ==> and (map isSame arr)

prop_take5 :: [a] -> Property
prop_take5 arr = ((length (take5 arr)) == 5) ==> True

qsort :: Ord a => [a] -> [a]
qsort []     = []
qsort (x:xs) = qsort lhs ++ [x] ++ qsort rhs
    where lhs = filter  (< x) xs
          rhs = filter (>= x) xs



main = quickCheckWith stdArgs {maxSuccess = 10, maxSize = 30} prop_reflexive
