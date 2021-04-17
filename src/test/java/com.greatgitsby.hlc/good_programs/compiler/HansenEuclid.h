{This is a HansenLite Program for computing the gcd of 2 numbers by the euclidian algorithm}

begin
   variable a;
   variable b;

   print "a is 732\n";
   print "b is 546\n";

   a := 732;
   b := 546;

   while b <> 0 do
      if a > b then
         a := a - b
      else
         b := b - a;

   print "the gcd of a and b is ";
   print a;
   print "\n"
end
