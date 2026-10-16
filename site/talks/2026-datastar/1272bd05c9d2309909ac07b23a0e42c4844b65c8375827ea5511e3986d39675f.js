
function reload_styles() {
  const id = 'ornament-styles'
  const oldLink = document.getElementById(id)
  fetch(oldLink.href).then((res)=>{
    res.text().then((css)=>{
      if(window.loaded_styles != css) {
        console.log("Reloading styles!")
        const newLink = document.createElement('link')
        newLink.id = id
        newLink.rel = 'stylesheet'
        newLink.href = '/styles.css?' + Date.now();
        newLink.onload = () => oldLink.remove()
        oldLink.after(newLink);
        window.loaded_styles = css
      }
    })
  })
}

function reload_slides() {
  fetch(window.location.href).then((res)=>{
    res.text().then((body)=>{
      if (window.slides_html === undefined) {
        window.slides_html = body
      } else if (window.slides_html != body) {
        window.slides_html = body
        console.log("Reloading slides!")
        // const parser = new DOMParser();
        // const dom = parser.parseFromString(body, "text/html");
        // const slides = dom.querySelector(".slides")
        // Reveal.destroy()
        // window.document.querySelector(".slides").innerHTML = slides.innerHTML
        // Reveal.initialize(window.reveal_opts)
        // Reveal.sync()
        window.location.reload()
      }
    })
  })
}

setInterval(() => {
  if (window.pause_reload) return
  reload_styles()
  reload_slides()
}, 1000);
