export const TeamCard = (props) => {
  return (
    <div className="teamCard">
      <img src={props.image} alt={props.imageDescription} />
      <div className="teamCardContents">
        <div className="teamCardName">{ props.name }</div>
        <div className="teamCardDescription">
          {props.description.map((section, val) => {
            return <div className="teamCardDescriptionSentence" key={val}>{section}</div>
          })}
        </div>
      </div>
    </div>
  )
}