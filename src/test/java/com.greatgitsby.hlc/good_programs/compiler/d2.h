{
Find how many times a number can be divided
by 2 before it ends up being less than 2
Assumes decimals are ignored and only whole
numbers are kept
}

begin
   variable u;

   variable v;

   u := 50;
   v := 0;

   while u > 1 do
   begin
      u := u / 2;
      v := v + 1
   end;

   print "Divided by 2 ";
   print v;
   print " times";
   print "\n"
end
