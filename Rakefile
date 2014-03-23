task :default => :deploy

desc 'Middleman build'
task :build do
  `middleman build`
end

desc 'Build and push out'
task :deploy => :build do
  puts `rsync -rv build/ arnebrasseur@arnebrasseur.net:subdomains/devblog`
end
task :push => :deploy
task :publish => :deploy
