a=1
for i in *.csv*; do
  new=$(printf "%d.csv" "$a") #04 pad to length of 4
  mv -- "$i" "$new"
  let a=a+1
done
