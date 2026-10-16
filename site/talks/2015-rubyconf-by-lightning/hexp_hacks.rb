class Hexp::Node
  def first(selector)
    select(selector).first
  end

  def delete(selector)
    replace(selector) { [] }
  end
end
