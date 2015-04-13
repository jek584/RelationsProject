-- Example.hs  --  Examples from HUnit user's guide
--
-- For more examples, check out the tests directory.  It contains unit tests
-- for HUnit. 
import Test.HUnit
import Test.HUnit.Tools
import Relation

second :: (a, b, c) -> b
second (_, b, _) = b

-- | takes in a triple with fileName, testName, and function to test. Returns a Test
testGenerator :: (String, String, ([(Int, Int)] -> Bool)) -> Test
testGenerator (fileName, testName, func) = TestCase (do input <- readFile fileName
                                                        assertEqual testName True (func (topairs input)))
testList :: Test
testList = TestList (map (\x -> TestLabel (second x) (testGenerator x)) [("sym.in", "Symmetric", issymmetric), ("trans.in", "Transitive", istransitive)])


symmTest :: Test
symmTest = TestCase (do input <- readFile "sym.in"
                        assertEqual "Symmetric" True (issymmetric (topairs input)))

tests :: Test
tests = TestList [TestLabel "test1" symmTest]

main = do runVerboseTests testList
